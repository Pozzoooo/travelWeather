package pozzo.apps.travelweather.map.viewmodel

import android.Manifest
import android.app.Application
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.splunk.mint.Mint
import pozzo.apps.tools.NetworkUtil
import pozzo.apps.travelweather.core.BaseViewModel
import pozzo.apps.travelweather.core.Error
import pozzo.apps.travelweather.forecast.ForecastBusiness
import pozzo.apps.travelweather.forecast.ForecastHelper
import pozzo.apps.travelweather.forecast.model.Weather
import pozzo.apps.travelweather.location.LocationBusiness
import pozzo.apps.travelweather.location.LocationLiveData
import pozzo.apps.travelweather.map.helper.GeoCoderHelper
import pozzo.apps.travelweather.map.viewrequest.ActionRequest
import pozzo.apps.travelweather.map.viewrequest.PermissionRequest
import java.io.IOException
import java.util.concurrent.Executors

/**
 * todo is my package strategy good?
 */
class MapViewModel(application: Application) : BaseViewModel(application) {
    private val locationBusiness = LocationBusiness()
    private val forecastBusiness = ForecastBusiness()
    private val geoCoderHelper = GeoCoderHelper(application)
    private val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(application)

    private var locationObserver: Observer<Location>? = null


//    todo should I create a bigger pool of threads or leave it small?
//      executor = ThreadPoolExecutor(
//    7, 20, 1, TimeUnit.SECONDS, LinkedBlockingQueue<Runnable>(7), ThreadPoolExecutor.DiscardPolicy()
//    )
    private val routeExecutor = Executors.newSingleThreadExecutor()
    private val addWeatherExecutor = Executors.newSingleThreadExecutor()
    private val mainThreadHandler = Handler()

    val startPosition = MutableLiveData<LatLng?>()
    val finishPosition = MutableLiveData<LatLng?>()
    val directionLine = MutableLiveData<PolylineOptions>()
    val weathers = MutableLiveData<List<Weather>>()
    val error = MutableLiveData<Error>()
    val actionRequest = MutableLiveData<ActionRequest>()
    val permissionRequest = MutableLiveData<PermissionRequest>()

    val isShowingProgress = MutableLiveData<Boolean>()
    val isShowingTopBar = MutableLiveData<Boolean>()
    val shouldFinish = MutableLiveData<Boolean>()

    init {
        isShowingProgress.value = false
        isShowingTopBar.value = false
    }

    fun setCurrentLocationAsStartStartedByUser(lifecycleOwner: LifecycleOwner) {
        setCurrentLocationAsStart(lifecycleOwner)
        sendFirebaseFabEvent()
    }

    private fun sendFirebaseFabEvent() {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "fab")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "currentLocation")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun setCurrentLocationAsStart(lifecycleOwner: LifecycleOwner) {
        setFinishPosition(null)
        setCurrentLocationAsStartPositionRequestingPermission(lifecycleOwner)
    }

    private fun setCurrentLocationAsStartPositionRequestingPermission(lifecycleOwner: LifecycleOwner) {
        val hasPermission = ContextCompat.checkSelfPermission(
                getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            setCurrentLocationAsStartPosition(lifecycleOwner)
        } else {
            permissionRequest.postValue(PermissionRequest(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)))
        }
    }

    private fun setCurrentLocationAsStartPosition(lifecycleOwner: LifecycleOwner) {
        val currentLocation = getCurrentLocation()
        if (currentLocation != null) {
            setStartPosition(currentLocation)
        } else {
            updateCurrentLocation(lifecycleOwner)
        }
    }

    fun onPermissionRequesteGranted(lifecycleOwner: LifecycleOwner) {
        //todo need to check what request exactly is, any polymorphic solution?
        setCurrentLocationAsStartPosition(lifecycleOwner)
    }

    private fun updateCurrentLocation(lifecycleOwner: LifecycleOwner) {
        showProgress()
        val locationLiveData = LocationLiveData[getApplication()]

        val locationObserver = Observer<Location> { location ->
            hideProgress()
            locationObserver = null
            if (location != null)
                setStartPosition(LatLng(location.latitude, location.longitude))
        }

        this.locationObserver = locationObserver
        locationLiveData.observe(lifecycleOwner, locationObserver)

        mainThreadHandler.postDelayed({
            val localLocationObserver = this.locationObserver
            if (localLocationObserver != null) {
                hideProgress()
                locationLiveData.removeObserver(localLocationObserver)
                error.postValue(Error.CANT_FIND_CURRENT_LOCATION)
            }
        }, 30000)
    }

    private fun showProgress() {
        isShowingProgress.postValue(true)
    }

    private fun hideProgress() {
        isShowingProgress.postValue(false)
    }

    fun getCurrentLocation(): LatLng? {
        try {
            val location = locationBusiness.getCurrentLocation(getApplication())
            if (location != null) {
                return LatLng(location.latitude, location.longitude)
            }
        } catch (e: SecurityException) {

        }

        return null
    }

    /**
     * Update route.
     */
     fun updateRoute() {
        showProgress()

        routeExecutor.execute({
            val direction = locationBusiness.getDirections(startPosition.value, finishPosition.value)
            if (direction?.isEmpty() == false) {
                setDirectionLine(direction)
                addWeathers(filterDirectionToWeatherPoints(direction))
            }
            hideProgress()
        })
    }

    private fun setDirectionLine(direction: List<LatLng>) {
        val rectLine = PolylineOptions().width(7F).color(Color.BLUE)
        direction.forEach {
            rectLine.add(it)
        }
        this.directionLine.postValue(rectLine)
    }

    private fun filterDirectionToWeatherPoints(direction: List<LatLng>) : List<LatLng> {
        val filteredPoints = ArrayList<LatLng>()
        var lastForecast = direction.get(0)
        for (i in 1 until direction.size - 1) {
            val latLng = direction.get(i)
            if (i % 250 == 1 //Um mod para nao checar em todos os pontos, sao muitos
                    && ForecastHelper.isMinDistanceToForecast(latLng, lastForecast)) {
                lastForecast = latLng
                filteredPoints.add(latLng)
            }
        }
        return filteredPoints
    }

    private fun addWeathers(weatherPoints: List<LatLng>) {
        addWeatherExecutor.execute({
            val newWeathers = requestWeathersFor(weatherPoints)
            val currentWeathers = this.weathers.value
            if (currentWeathers?.isEmpty() == false)
                newWeathers.addAll(currentWeathers)
            this.weathers.postValue(newWeathers)
        })
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
        if (finishPosition != null)
            addWeathers(listOf(finishPosition))
    }

    fun setStartPosition(startPosition: LatLng?) {
        this.startPosition.postValue(startPosition)
        if (startPosition != null)
            addWeathers(listOf(startPosition))
    }

    fun back() {
        when {
            isShowingTopBar.value == true -> hideTopBar()
            finishPosition.value != null -> setFinishPosition(null)
            startPosition.value != null -> setStartPosition(null)
            else -> shouldFinish.postValue(true)
        }
    }

    fun toggleTopBar() = if (isShowingTopBar.value != true) displayTopBar() else hideTopBar()
    fun displayTopBar() = isShowingTopBar.postValue(true)
    fun hideTopBar() = isShowingTopBar.postValue(false)

    fun getRouteBounds() : LatLngBounds? {
        return if (isFullRouteSelected()) {
            LatLngBounds.builder()
                    .include(startPosition.value)
                    .include(finishPosition.value).build()
        } else {
            null
        }
    }

    fun isFullRouteSelected() : Boolean = startPosition.value != null && finishPosition.value != null

    fun addPoint(latLng: LatLng) {
        //todo what about create a polymorphsm on something like "currentSelection", so at least 1 if is avoided
        hideTopBar()
        if (!checkConnection()) {
            error.postValue(Error.NO_CONNECTION)
        } else if (startPosition.value == null) {
            setStartPosition(latLng)
        } else {
            setFinishPosition(latLng)
        }
    }

    private fun checkConnection() : Boolean = NetworkUtil.isNetworkAvailable(getApplication())

    fun searchAddress(string: String) {
        try {
            val location = geoCoderHelper.getPositionFromFirst(string)
            addPoint(location)
        } catch (e: IOException) {
            error.postValue(Error.ADDRESS_NOT_FOUND)
        }
    }

    fun dismissError() {
        error.value = null
    }

    fun requestClear() {
        hideTopBar()
        actionRequest.postValue(ActionRequest.CLEAR)
    }

    fun actionRequestAccepted(actionRequest: ActionRequest) {
        //todo solve it with pollymorphsm?
        when(actionRequest) {
            ActionRequest.CLEAR -> {
                setStartPosition(null)
                setFinishPosition(null)
            }
        }
    }

    fun actionRequestDismissed() {
        this.actionRequest.value = null
    }
}
