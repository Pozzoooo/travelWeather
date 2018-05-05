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
import pozzo.apps.travelweather.forecast.model.Weather
import pozzo.apps.travelweather.location.LocationBusiness
import pozzo.apps.travelweather.location.LocationLiveData
import pozzo.apps.travelweather.location.helper.GeoCoderHelper
import pozzo.apps.travelweather.map.action.ActionRequest
import pozzo.apps.travelweather.map.action.ClearActionRequest
import pozzo.apps.travelweather.map.firebase.MapAnalytics
import pozzo.apps.travelweather.map.viewrequest.LocationPermissionRequest
import pozzo.apps.travelweather.map.viewrequest.PermissionRequest
import java.io.IOException
import java.util.concurrent.Executors

class MapViewModel(application: Application) : BaseViewModel(application) {
    private val locationBusiness = LocationBusiness()
    private val forecastBusiness = ForecastBusiness()
    private val geoCoderHelper = GeoCoderHelper(application)
    private val mapAnalytics = MapAnalytics(FirebaseAnalytics.getInstance(application))

    private val routeExecutor = Executors.newSingleThreadExecutor()
    private val addWeatherExecutor = Executors.newSingleThreadExecutor()

    val startPosition = MutableLiveData<LatLng?>()
    val finishPosition = MutableLiveData<LatLng?>()
    val directionLine = MutableLiveData<PolylineOptions>()
    val weathers = MutableLiveData<List<Weather>>()
    val error = MutableLiveData<Error>()
    val warning = MutableLiveData<Warning>()
    val actionRequest = MutableLiveData<ActionRequest>()
    val permissionRequest = MutableLiveData<PermissionRequest>()

    val isShowingProgress = MutableLiveData<Boolean>()
    val isShowingTopBar = MutableLiveData<Boolean>()
    val shouldFinish = MutableLiveData<Boolean>()

    var dragStart = 0L

    private val finishObserver = Observer<LatLng?> {
        if (it != null) {
            addWeathers(setOf(it))
            updateRoute()
        }
    }

    private val startObserver = Observer<LatLng?> {
        if (it != null)
            addWeathers(setOf(it))
    }

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
        finishPosition.observeForever(finishObserver)
        startPosition.observeForever(startObserver)
        error.observeForever(errorObserver)
    }

    override fun onCleared() {
        super.onCleared()
        finishPosition.removeObserver(finishObserver)
        startPosition.removeObserver(startObserver)
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
    }

    fun onPermissionDenied(permissionRequest: PermissionRequest) {
        permissionRequest.denied()
    }

    fun warn(warning: Warning) {
        this.warning.postValue(warning)
    }

    private fun updateCurrentLocation(lifecycleOwner: LifecycleOwner) {
        showProgress()

        val locationLiveData = LocationLiveData(getApplication())
        var locationObserver : Observer<Location>? = null
        locationObserver = Observer { location ->
            locationLiveData.removeObserver(locationObserver!!)

            //todo there is room to improve this progress binding
            if (isShowingProgress.value == true) {
                hideProgress()
                if (location != null) {
                    setStartPosition(LatLng(location.latitude, location.longitude))
                } else {
                    error.postValue(Error.CANT_FIND_CURRENT_LOCATION)
                }
            }
        }
        locationLiveData.observeWithTimeout(lifecycleOwner, locationObserver, 30000L)
    }

    private fun showProgress() {
        isShowingProgress.postValue(true)
    }

    fun hideProgress() {
        isShowingProgress.postValue(false)
    }

    private fun updateRoute() {
        showProgress()

        routeExecutor.execute({
            val direction = locationBusiness.getDirections(startPosition.value, finishPosition.value)
            if (direction?.isEmpty() == false) {
                setDirectionLine(direction)
                addWeathers(filterDirectionToWeatherPoints(direction))
            } else {
                this.error.postValue(Error.CANT_FIND_ROUTE)
            }
            hideProgress()
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

    private fun addWeathers(weatherPoints: Set<LatLng>) {
        //todo need to add the weathers progressively, so the user wont wait for a long time
        addWeatherExecutor.execute({
            val filteredPoints = removeAlreadyUsedLatLng(weatherPoints)
            if (filteredPoints.isEmpty()) {
                this.weathers.postValue(this.weathers.value)
            } else {
                addWeathers(requestWeathersFor(filteredPoints))
            }
        })
    }

    private fun removeAlreadyUsedLatLng(weatherPoints: Set<LatLng>) : List<LatLng> =
        weatherPoints.filter { !containsLatLng(it) }

    private fun containsLatLng(latLng: LatLng) : Boolean =
        weathers.value?.firstOrNull { latLng == it.latLng } != null

    private fun addWeathers(weathers: ArrayList<Weather>) {
        val currentWeathers = this.weathers.value
        if (currentWeathers?.isEmpty() == false)
            weathers.addAll(currentWeathers)
        this.weathers.postValue(weathers)
    }

    private fun requestWeathersFor(weatherPoints: List<LatLng>) : ArrayList<Weather> {
        val weathers = ArrayList<Weather>()
        weatherPoints.forEach {
            try {
                weathers.add(forecastBusiness.from(it))
            } catch (e: ClassCastException) {
                //Business don't want't to send us this forecast
                //This one is known server issue and won't be logged
                //todo improve it for a more specific exception
            } catch (e: Exception) {
                Mint.logException(e)
            }
        }
        return weathers
    }

    fun setFinishPosition(finishPosition: LatLng?) {
        this.finishPosition.postValue(finishPosition)
    }

    fun setStartPosition(startPosition: LatLng?) {
        this.startPosition.postValue(startPosition)
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
    }

    fun actionRequestDismissed() {
        this.actionRequest.value = null
    }

    fun drawerMenuOpened() {
        mapAnalytics.sendDrawerOpened()
    }
}
