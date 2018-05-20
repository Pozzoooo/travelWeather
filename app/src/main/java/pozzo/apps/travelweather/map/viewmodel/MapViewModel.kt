package pozzo.apps.travelweather.map.viewmodel

import android.Manifest
import android.app.Application
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.support.v4.content.ContextCompat
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.splunk.mint.Mint
import pozzo.apps.tools.NetworkUtil
import pozzo.apps.travelweather.App
import pozzo.apps.travelweather.core.BaseViewModel
import pozzo.apps.travelweather.core.Error
import pozzo.apps.travelweather.core.Warning
import pozzo.apps.travelweather.forecast.ForecastBusiness
import pozzo.apps.travelweather.forecast.ForecastHelper
import pozzo.apps.travelweather.forecast.model.*
import pozzo.apps.travelweather.location.LocationBusiness
import pozzo.apps.travelweather.location.LocationLiveData
import pozzo.apps.travelweather.location.helper.GeoCoderHelper
import pozzo.apps.travelweather.map.action.ActionRequest
import pozzo.apps.travelweather.map.action.ClearActionRequest
import pozzo.apps.travelweather.map.business.PreferencesBusiness
import pozzo.apps.travelweather.map.firebase.MapAnalytics
import pozzo.apps.travelweather.map.userinputrequest.LocationPermissionRequest
import pozzo.apps.travelweather.map.userinputrequest.PermissionRequest
import java.io.IOException
import java.net.UnknownHostException
import java.util.concurrent.Executors

class MapViewModel(application: Application) : BaseViewModel(application) {
    private val locationBusiness = LocationBusiness()
    private val forecastBusiness = ForecastBusiness()
    private val geoCoderHelper = GeoCoderHelper(application)
    private val mapAnalytics = MapAnalytics(FirebaseAnalytics.getInstance(application))
    private val preferencesBusiness = PreferencesBusiness(application)

    private val routeExecutor = Executors.newSingleThreadExecutor()
    private val addWeatherExecutor = Executors.newSingleThreadExecutor()

    private var dragStart = 0L
    private val locationLiveData = LocationLiveData(getApplication())
    private var locationObserver: Observer<Location>? = null

    val route = MutableLiveData<Route>()
    val cameraState = MutableLiveData<CameraUpdate>()

    val error = MutableLiveData<Error>()
    val warning = MutableLiveData<Warning>()
    val actionRequest = MutableLiveData<ActionRequest>()
    val permissionRequest = MutableLiveData<PermissionRequest>()

    val isShowingProgress = MutableLiveData<Boolean>()
    val isShowingTopBar = MutableLiveData<Boolean>()
    val shouldFinish = MutableLiveData<Boolean>()

    init {
        isShowingProgress.value = false
        isShowingTopBar.value = false
        shouldFinish.value = false
        route.value = Route()
    }

    fun onMapReady(lifecycleOwner: LifecycleOwner) {
        if (route.value?.startPoint == null)
            setCurrentLocationAsStartPositionRequestingPermission(lifecycleOwner)
    }

    fun setStartAsCurrentLocationRequestedByUser(lifecycleOwner: LifecycleOwner) {
        setCurrentLocationAsStartPositionRequestingPermission(lifecycleOwner)
        mapAnalytics.sendFirebaseUserRequestedCurrentLocationEvent()
    }

    private fun setCurrentLocationAsStartPositionRequestingPermission(lifecycleOwner: LifecycleOwner) {
        if (hasLocationPermission()) {
            setCurrentLocationAsStartPosition(lifecycleOwner)
        } else {
            requestLocationPermission()
        }
    }

    private fun hasLocationPermission() : Boolean = ContextCompat.checkSelfPermission(
                getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission() {
        permissionRequest.postValue(LocationPermissionRequest(this))
    }

    fun setCurrentLocationAsStartPosition(lifecycleOwner: LifecycleOwner) {
        val currentLocation = getCurrentKnownLocation()
        if (currentLocation != null) {
            setStartPosition(currentLocation)
        } else {
            updateCurrentLocation(lifecycleOwner)
        }
    }

    private fun getCurrentKnownLocation(): LatLng? {
        try {
            val location = locationBusiness.getCurrentKnownLocation(getApplication())
            return if (location != null) LatLng(location.latitude, location.longitude) else null
        } catch (e: SecurityException) {
            //we might not have permission, we leave the system try to activate the gps before any message
        } catch (e: Exception) {
            Mint.logException(e)
        }
        return null
    }

    fun onPermissionGranted(permissionRequest: PermissionRequest, lifecycleOwner: LifecycleOwner) {
        permissionRequest.granted(lifecycleOwner)
        this.permissionRequest.value = null
    }

    fun onPermissionDenied(permissionRequest: PermissionRequest) {
        permissionRequest.denied()
        this.permissionRequest.value = null
    }

    fun warn(warning: Warning) {
        this.warning.postValue(warning)
    }

    private fun updateCurrentLocation(lifecycleOwner: LifecycleOwner) {
        showProgress()

        val locationObserver = Observer<Location> { location ->
            removeLocationObserver()
            if (location != null) {
                setStartPosition(LatLng(location.latitude, location.longitude))
            } else {
                postError(Error.CANT_FIND_CURRENT_LOCATION)
            }
        }
        locationLiveData.observeWithTimeout(lifecycleOwner, locationObserver, 30000L)
        this.locationObserver = locationObserver
    }

    private fun removeLocationObserver() {
        hideProgress()
        locationObserver?.let {
            locationLiveData.removeObserver(it)
        }
    }

    private fun postError(error: Error) {
        this.error.postValue(error)
        mapAnalytics.sendErrorMessage(error)
    }

    private fun showProgress() {
        isShowingProgress.postValue(true)
    }

    private fun hideProgress() {
        isShowingProgress.postValue(false)
    }

    //todo requires redability improvements here
    private fun filterDirectionToWeatherPoints(direction: List<LatLng>) : List<LatLng> {
        val filteredPoints = ArrayList<LatLng>()
        val directionSize = direction.size

        if (directionSize == 0) {
            return filteredPoints
        }

        if (directionSize < 1000) {
            filteredPoints.add(direction[directionSize / 2])
            return filteredPoints
        }

        val firstPoint = direction[350]
        val lastPoint = direction[directionSize - 350]
        filteredPoints.add(firstPoint)
        filteredPoints.add(lastPoint)

        var lastForecast = firstPoint
        for (i in 500 until directionSize - 500) {
            val latLng = direction[i]
            if (i % 250 == 1 //Um mod para nao checar em todos os pontos, sao muitos
                    && ForecastHelper.isMinDistanceToForecast(latLng, lastForecast)) {
                lastForecast = latLng
                filteredPoints.add(latLng)
            }
        }
        return filteredPoints
    }

    private fun requestWeathersFor(weatherPoints: List<LatLng>) : ArrayList<Weather> {
        val weathers = ArrayList<Weather>()
        weatherPoints.forEach {
            try {
                weathers.add(requestWeatherFor(it))
            } catch (e: UnknownHostException) {
                postError(Error.NO_CONNECTION)
            } catch (e: Exception) {
                Mint.logException(e)
            }
        }
        return weathers
    }

    private fun requestWeatherFor(weatherPoint: LatLng) : Weather = forecastBusiness.from(weatherPoint)

    private fun parseWeatherIntoMapPoints(weathers: ArrayList<Weather>) : ArrayList<MapPoint> {
        val mapPoints = ArrayList<MapPoint>()

        weathers.forEach {
            parseWeatherIntoMapPoint(it)?.let { mapPoints.add(it) }
        }

        return mapPoints
    }

    private fun parseWeatherIntoMapPoint(weather: Weather) : MapPoint? {
        if (weather.address != null) {
            val selectedDay = preferencesBusiness.getSelectedDay()
            val forecast = weather.getForecast(selectedDay)
            return MapPoint(forecast.icon, forecast.text, weather.latLng, weather.url)
        }
        return null
    }

    fun setFinishPosition(finishPosition: LatLng?) {
        //todo there is a crash here, when I try to clear the map route
        cameraState.postValue(CameraUpdateFactory.newLatLngBounds(
                LatLngBounds.builder()
                    .include(route.value!!.startPoint?.position)
                    .include(finishPosition).build(),
                70))
        removeLocationObserver()
        if (finishPosition != null) {
            createFinishPoint(finishPosition)
        } else {
            route.value = Route(startPoint = route.value!!.startPoint)
        }
    }

    private fun createFinishPoint(finishPosition: LatLng) {
        val startPoint = route.value!!.startPoint!!
        val mapPoint = FinishPoint(getApplication<App>().resources, finishPosition)
        val route = Route(startPoint = startPoint, finishPoint = mapPoint)
        this.route.postValue(route)
        updateRoute(startPoint.position, finishPosition)
    }

    private fun updateRoute(startPosition: LatLng, finishPosition: LatLng) {
        showProgress()

        routeExecutor.execute({
            val direction = locationBusiness.getDirections(startPosition, finishPosition)
            if (direction?.isEmpty() == false) {
                val directionLine = setDirectionLine(direction)
                val mapPoints = toMapPoints(filterDirectionToWeatherPoints(direction))
                route.postValue(Route(route.value, polyline = directionLine, mapPoints = mapPoints))
            } else {
                postError(Error.CANT_FIND_ROUTE)
            }
            hideProgress()
        })
    }

    private fun setDirectionLine(direction: List<LatLng>) : PolylineOptions =
            PolylineOptions().width(7F).color(Color.BLUE).addAll(direction)

    private fun toMapPoints(weatherPoints: List<LatLng>) : List<MapPoint> {
        val weathers = requestWeathersFor(weatherPoints)
        return parseWeatherIntoMapPoints(weathers)
    }

    fun setStartPosition(startPosition: LatLng?) {
        //todo should camera state be parte of the Route?
        cameraState.postValue(CameraUpdateFactory.newLatLngZoom(startPosition, 8f))
        removeLocationObserver()
        if (startPosition != null) {
            createStartPoint(startPosition)
        } else {
            route.value = Route()
        }
    }

    private fun createStartPoint(startPosition: LatLng) {
        val finishPoint = this.route.value!!.finishPoint
        val mapPoint = StartPoint(getApplication<App>().resources, startPosition)
        val route = Route(startPoint = mapPoint, finishPoint = finishPoint)
        this.route.postValue(route)
        finishPoint?.let { updateRoute(startPosition, it.position) }
    }

    fun back() {
        when {
            isShowingTopBar.value == true -> hideTopBar()
            route.value?.finishPoint != null -> setFinishPosition(null)
            route.value?.startPoint != null -> setStartPosition(null)
            else -> shouldFinish.postValue(true)
        }
    }

    fun toggleTopBar(text: String) {
        if (isShowingTopBar.value != true) {
            displayTopBar()
        } else {
            hideTopBar()
            if (text.isNotBlank())
                searchAddress(text)
        }
    }

    private fun displayTopBar() {
        isShowingTopBar.postValue(true)
        mapAnalytics.sendDisplayTopBarAction()
    }

    private fun hideTopBar() = isShowingTopBar.postValue(false)

    fun finishFlagDragActionStarted() {
        mapAnalytics.sendDragFinishEvent()
        dragStart = System.currentTimeMillis()
    }

    fun finishFlagDragActionFinished(latLng: LatLng) {
        addPoint(latLng)
        mapAnalytics.sendDragDurationEvent("finishFlag", System.currentTimeMillis() - dragStart)
    }

    fun addPoint(latLng: LatLng) {
        hideTopBar()
        if (!NetworkUtil.isNetworkAvailable(getApplication())) {
            postError(Error.NO_CONNECTION)
        } else if (route.value!!.startPoint == null) {
            setStartPosition(latLng)
        } else {
            setFinishPosition(latLng)
        }
    }

    fun searchAddress(string: String) {
        try {
            mapAnalytics.sendSearchAddress()
            val addressLatLng = geoCoderHelper.getPositionFromFirst(string)
            if (addressLatLng != null)
                addPoint(addressLatLng)
            else
                postError(Error.ADDRESS_NOT_FOUND)
        } catch (e: IOException) {
            postError(Error.NO_CONNECTION)
        }
    }

    fun errorDismissed() {
        error.value = null
    }

    fun requestClearRequestedByUser() {
        mapAnalytics.sendClearRouteEvent()
        requestClear()
    }

    fun requestClear() {
        hideTopBar()
        actionRequest.postValue(ClearActionRequest(this))
    }

    fun actionRequestAccepted(actionRequest: ActionRequest) {
        actionRequest.execute()
        this.actionRequest.value = null
    }

    fun actionRequestDismissed() {
        this.actionRequest.value = null
    }

    fun drawerMenuOpened() {
        mapAnalytics.sendDrawerOpened()
    }
}
