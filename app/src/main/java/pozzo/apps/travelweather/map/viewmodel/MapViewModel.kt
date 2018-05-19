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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.splunk.mint.Mint
import pozzo.apps.tools.NetworkUtil
import pozzo.apps.travelweather.core.BaseViewModel
import pozzo.apps.travelweather.core.Error
import pozzo.apps.travelweather.core.Warning
import pozzo.apps.travelweather.forecast.ForecastBusiness
import pozzo.apps.travelweather.forecast.ForecastHelper
import pozzo.apps.travelweather.forecast.model.FinishPoint
import pozzo.apps.travelweather.forecast.model.MapPoint
import pozzo.apps.travelweather.forecast.model.StartPoint
import pozzo.apps.travelweather.forecast.model.Weather
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

    //todo is it possibility to hide all this observers and expose an observe method? So I can make them mutable only inside this class
    val startPosition = MutableLiveData<LatLng?>()
    val finishPosition = MutableLiveData<LatLng?>()
    val directionLine = MutableLiveData<PolylineOptions>()
    val mapPoints = MutableLiveData<List<MapPoint>>()
    val error = MutableLiveData<Error>()
    val warning = MutableLiveData<Warning>()
    val actionRequest = MutableLiveData<ActionRequest>()
    val permissionRequest = MutableLiveData<PermissionRequest>()

    val isShowingProgress = MutableLiveData<Boolean>()
    val isShowingTopBar = MutableLiveData<Boolean>()
    val shouldFinish = MutableLiveData<Boolean>()

    //todo I don't really need this observer either, should create a "postError()"
    private val errorObserver = Observer<Error?> {
        if (it != null)
            mapAnalytics.sendErrorMessage(it)
    }

    init {
        isShowingProgress.value = false
        isShowingTopBar.value = false
        shouldFinish.value = false
        registerObservers()
    }

    private fun registerObservers() {
        error.observeForever(errorObserver)
    }

    override fun onCleared() {
        super.onCleared()
        error.removeObserver(errorObserver)
    }

    fun onMapReady(lifecycleOwner: LifecycleOwner) {
        if (startPosition.value == null)
            setCurrentLocationAsStart(lifecycleOwner)
    }

    fun setStartAsCurrentLocationRequestedByUser(lifecycleOwner: LifecycleOwner) {
        setCurrentLocationAsStart(lifecycleOwner)
        mapAnalytics.sendFirebaseUserRequestedCurrentLocationEvent()
    }

    fun setCurrentLocationAsStart(lifecycleOwner: LifecycleOwner) {
        setFinishPosition(null)
        setCurrentLocationAsStartPositionRequestingPermission(lifecycleOwner)
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

    //todo if user starts using it before it actually find it, I need to cancel it or at least
    //  give some better feedback on whats going on
    private fun updateCurrentLocation(lifecycleOwner: LifecycleOwner) {
        showProgress()

        val locationLiveData = LocationLiveData(getApplication())
        var locationObserver : Observer<Location>? = null
        locationObserver = Observer { location ->
            locationLiveData.removeObserver(locationObserver!!)

            hideProgress()
            if (location != null) {
                setStartPosition(LatLng(location.latitude, location.longitude))
            } else {
                error.postValue(Error.CANT_FIND_CURRENT_LOCATION)
            }
        }
        locationLiveData.observeWithTimeout(lifecycleOwner, locationObserver, 30000L)
    }

    private fun showProgress() {
        isShowingProgress.postValue(true)
    }

    private fun hideProgress() {
        isShowingProgress.postValue(false)
    }

    private fun updateRoute(startPosition: LatLng, finishPosition: LatLng) {
        showProgress()

        routeExecutor.execute({
            val direction = locationBusiness.getDirections(startPosition, finishPosition)
            if (direction?.isEmpty() == false) {
                setDirectionLine(direction)
                addMapPoints(filterDirectionToWeatherPoints(direction))
            } else {
                this.error.postValue(Error.CANT_FIND_ROUTE)
            }
        })
    }

    private fun setDirectionLine(direction: List<LatLng>) {
        val rectLine = PolylineOptions().width(7F).color(Color.BLUE).addAll(direction)
        this.directionLine.postValue(rectLine)
    }

    private fun filterDirectionToWeatherPoints(direction: List<LatLng>) : Set<LatLng> {
        val filteredPoints = HashSet<LatLng>()
        var lastForecast = direction[0]
        for (i in 1 until direction.size - 1) {
            val latLng = direction[i]
            if (i % 250 == 1 //Um mod para nao checar em todos os pontos, sao muitos
                    && ForecastHelper.isMinDistanceToForecast(latLng, lastForecast)) {
                lastForecast = latLng
                filteredPoints.add(latLng)
            }
        }
        return filteredPoints
    }

    private fun addMapPoints(weatherPoints: Set<LatLng>) {
        showProgress()
        addWeatherExecutor.execute({
            val filteredPoints = removeAlreadyUsedLatLng(weatherPoints)
            val weathers = requestWeathersFor(filteredPoints)
            val mapPoints = parseWeatherIntoMapPoints(weathers)
            addMapPoints(mapPoints)
            hideProgress()
        })
    }

    private fun removeAlreadyUsedLatLng(weatherPoints: Set<LatLng>) : List<LatLng> =
        weatherPoints.filter { !containsLatLng(it) }

    private fun containsLatLng(latLng: LatLng) : Boolean =
        mapPoints.value?.firstOrNull { latLng == it.position } != null

    private fun requestWeathersFor(weatherPoints: List<LatLng>) : ArrayList<Weather> {
        val weathers = ArrayList<Weather>()
        weatherPoints.forEach {
            try {
                weathers.add(requestWeatherFor(it))
            } catch (e: UnknownHostException) {
                this.error.postValue(Error.NO_CONNECTION)
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

    private fun addMapPoints(mapPoints: ArrayList<MapPoint>) {
        val currentWeathers = this.mapPoints.value
        if (currentWeathers?.isEmpty() == false)
            mapPoints.addAll(currentWeathers)

        this.mapPoints.postValue(mapPoints)
    }

    fun setFinishPosition(finishPosition: LatLng?) {
        this.finishPosition.postValue(finishPosition)

        if (finishPosition != null) {
            createFinishPoint(finishPosition)
        }
    }

    //todo quando atualizar a rota preciso limpar a tela, isso talvez jah ajude na questao das bandeira perdidas na tela
    private fun createFinishPoint(finishPosition: LatLng) {
        addWeatherExecutor.execute({
            val weather = requestWeathersFor(listOf(finishPosition)).getOrNull(0)

            if (weather?.address != null) {
                val selectedDay = preferencesBusiness.getSelectedDay()
                val forecast = weather.getForecast(selectedDay)
                val mapPoint = FinishPoint(forecast.text, weather.latLng, weather.url)
                addMapPoints(arrayListOf<MapPoint>(mapPoint))
            }
        })

        updateRoute(startPosition.value!!, finishPosition)
    }

    //todo preciso que ele possa ser setada e atualizar a rota automaticamente (quando ele arrasta a bandeira)
    fun setStartPosition(startPosition: LatLng?) {
        this.startPosition.postValue(startPosition)

        if (startPosition != null) {
            createStartPoint(startPosition)
        }
    }

    private fun createStartPoint(startPosition: LatLng) {
        addWeatherExecutor.execute({
            val weather = requestWeathersFor(listOf(startPosition)).getOrNull(0)

            if (weather?.address != null) {
                val selectedDay = preferencesBusiness.getSelectedDay()
                val forecast = weather.getForecast(selectedDay)
                val mapPoint = StartPoint(forecast.text, weather.latLng, weather.url)
                addMapPoints(arrayListOf<MapPoint>(mapPoint))
            }
        })
    }

    fun back() {
        when {
            isShowingTopBar.value == true -> hideTopBar()
            finishPosition.value != null -> setFinishPosition(null)
            startPosition.value != null -> setStartPosition(null)
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

    fun getRouteBounds() : LatLngBounds? {
        return if (isFullRouteSelected()) {
            LatLngBounds.builder()
                    .include(startPosition.value)
                    .include(finishPosition.value).build()
        } else {
            null
        }
    }

    private fun isFullRouteSelected() : Boolean = startPosition.value != null && finishPosition.value != null

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
            error.postValue(Error.NO_CONNECTION)
        } else if (startPosition.value == null) {
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
                error.postValue(Error.ADDRESS_NOT_FOUND)
        } catch (e: IOException) {
            error.postValue(Error.NO_CONNECTION)
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
