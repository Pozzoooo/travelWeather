package pozzo.apps.travelweather.map.viewmodel

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import pozzo.apps.travelweather.analytics.MapAnalytics
import pozzo.apps.travelweather.common.NetworkHelper
import pozzo.apps.travelweather.common.business.PreferencesBusiness
import pozzo.apps.travelweather.core.BaseViewModel
import pozzo.apps.travelweather.core.CoroutineSettings.background
import pozzo.apps.travelweather.core.Error
import pozzo.apps.travelweather.core.LastRunRepository
import pozzo.apps.travelweather.core.Warning
import pozzo.apps.travelweather.core.action.ActionRequest
import pozzo.apps.travelweather.core.action.ClearActionRequest
import pozzo.apps.travelweather.core.action.RateMeActionRequest
import pozzo.apps.travelweather.core.userinputrequest.LocationPermissionRequest
import pozzo.apps.travelweather.core.userinputrequest.PermissionRequest
import pozzo.apps.travelweather.direction.DirectionNotFoundException
import pozzo.apps.travelweather.forecast.model.Day
import pozzo.apps.travelweather.forecast.model.DayTime
import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.forecast.model.Time
import pozzo.apps.travelweather.forecast.model.point.FinishPoint
import pozzo.apps.travelweather.forecast.model.point.StartPoint
import pozzo.apps.travelweather.forecast.model.point.WeatherPoint
import pozzo.apps.travelweather.location.CurrentLocationRequester
import pozzo.apps.travelweather.location.GeoCoderBusiness
import pozzo.apps.travelweather.location.PermissionDeniedException
import pozzo.apps.travelweather.map.MapSettings
import pozzo.apps.travelweather.map.overlay.LastRunKey
import pozzo.apps.travelweather.map.overlay.MapTutorialScript
import pozzo.apps.travelweather.map.parser.WeatherPointsAdapter
import pozzo.apps.travelweather.route.RequestLimitReached
import pozzo.apps.travelweather.route.RouteBusiness
import java.io.IOException
import javax.inject.Inject

@ActivityScoped
class MapViewModel @Inject constructor(
        application: Application, private val currentLocationRequester: CurrentLocationRequester,
        private val mapTutorialScript: MapTutorialScript, private val mapSettings: MapSettings,
        private val preferencesBusiness: PreferencesBusiness
) : BaseViewModel(application), ErrorHandler {

    @Inject protected lateinit var geoCoderBusiness: GeoCoderBusiness
    @Inject protected lateinit var mapAnalytics: MapAnalytics
    @Inject protected lateinit var routeBusiness: RouteBusiness
    @Inject protected lateinit var lastRunRepository: LastRunRepository
    @Inject protected lateinit var networkHelper: NetworkHelper

    private var dragStart = 0L

    private var updateRouteJob: Job? = null
    private var route = Route()
    private val weatherPointsAdapter: WeatherPointsAdapter
    private var selectedTime = Time.getDefault()

    private val job = Job()
    private val scope = CoroutineScope(job + background)

    val routeData = MutableLiveData<Route>()
    val weatherPointsData = MutableLiveData<Channel<WeatherPoint>>()
    val mapSettingsData = MutableLiveData<MapSettings>()

    val error = MutableLiveData<Error>()
    val warning = MutableLiveData<Warning>()
    val actionRequest = MutableLiveData<ActionRequest>()
    val permissionRequest = MutableLiveData<PermissionRequest>()
    val overlay = MutableLiveData<LastRunKey>()
    val selectedDayTime = MutableLiveData<DayTime>()

    val isShowingProgress = MutableLiveData<Boolean>()
    val isShowingSearch = MutableLiveData<Boolean>()
    val shouldFinish = MutableLiveData<Boolean>()

    init {
        job.start()

        isShowingProgress.value = false
        isShowingSearch.value = false
        shouldFinish.value = false
        routeData.value = route
        currentLocationRequester.callback = CurrentLocationBinder(currentLocationRequester, this) {
            setStartPosition(it)
        }
        mapTutorialScript.playTutorialCallback = { overlay.postValue(it) }
        mapTutorialScript.onAppStart()
        mapSettingsData.postValue(mapSettings)
        selectedDayTime.value = getSelectedDayTime()
        weatherPointsAdapter = WeatherPointsAdapter(weatherPointsData, scope)
    }

    fun onMapReady(lifecycleOwner: LifecycleOwner) {
        if (route.startPoint == null) setStartAsCurrentLocation(lifecycleOwner)
    }

    private fun setStartAsCurrentLocation(lifecycleOwner: LifecycleOwner) {
        try {
            currentLocationRequester.requestCurrentLocationRequestingPermission(lifecycleOwner)
        } catch (e: PermissionDeniedException) {
            permissionRequest.postValue(LocationPermissionRequest(
                    OnLocationPermissionChange(currentLocationRequester, warning) {
                        mapSettingsData.postValue(mapSettings)
                    }))
        }
    }

    override fun onCleared() {
        job.complete()
        super.onCleared()
    }

    fun setStartAsCurrentLocationRequestedByUser(lifecycleOwner: LifecycleOwner) {
        mapAnalytics.sendFirebaseUserRequestedCurrentLocationEvent()
        setStartAsCurrentLocation(lifecycleOwner)
        mapTutorialScript.onUserRequestCurrentLocation()
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

    fun errorDismissed() {
        error.value = null
    }

    fun clearStartPosition() {
        setRoute(Route(finishPoint = route.finishPoint))
    }

    private fun setRoute(route: Route) {
        this.route = route
        routeData.postValue(route)
    }

    fun clearFinishPosition() {
        setRoute(Route(startPoint = route.startPoint))
    }

    fun setStartPosition(startPosition: LatLng) {
        val startPoint = StartPoint(startPosition)
        updateRoute(startPoint = startPoint)
        logDragEvent("re-startFlag")
    }

    private fun updateRoute(startPoint: StartPoint? = route.startPoint, finishPoint: FinishPoint? = route.finishPoint) {
        setRoute(Route(startPoint = startPoint, finishPoint = finishPoint))
        if (startPoint == null || finishPoint == null) return

        showProgress()
        updateRouteJob?.cancel()
        updateRouteJob = scope.launch(background) {
            try {
                val route = routeBusiness.createRoute(startPoint, finishPoint)
                if (isActive) {
                    setRoute(route)
                    weatherPointsAdapter.updateWeatherPoints(getSelectedDayTime(), route)
                }
            } catch (e: DirectionNotFoundException) {
                postError(Error.CANT_FIND_ROUTE)
            } catch (e: RequestLimitReached) {
                postError(Error.LIMIT_REACHED)
            } catch (e: IOException) {
                handleConnectionError(e)
            } finally {
                hideProgress()
            }
        }
    }

    fun setFinishPosition(finishPosition: LatLng) {
        val finishPoint = FinishPoint(finishPosition)
        updateRoute(finishPoint = finishPoint)
        mapTutorialScript.onFinishPositionSet()
        logDragEvent("re-finishFlag")
    }

    //TODO I think I should isolate this one
    override fun postError(error: Error) {
        this.error.postValue(error)
        mapAnalytics.sendErrorMessage(error)
    }

    private fun showProgress() {
        isShowingProgress.postValue(true)
    }

    private fun hideProgress() {
        isShowingProgress.postValue(false)
    }

    private fun handleConnectionError(ioException: IOException) {
        if (error.value != null) return //there is a popup showing already, so no bother
        if (!networkHelper.isConnected(getApplication())) postError(Error.NO_CONNECTION)
        else postError(Error.CANT_REACH)
    }

    fun back() {
        when {
            isShowingSearch.value == true -> hideSearch()
            route.finishPoint != null -> clearFinishPosition()
            route.startPoint != null -> clearStartPosition()
            else -> shouldFinish.postValue(true)
        }
    }

    fun toggleSearch(text: String) {
        if (isShowingSearch.value != true) {
            showSearch()
        } else {
            hideSearch()
            if (text.isNotBlank()) searchAddress(text)
        }
    }

    private fun showSearch() {
        isShowingSearch.postValue(true)
        mapAnalytics.sendShowSearch()
    }

    private fun hideSearch() {
        isShowingSearch.postValue(false)
        mapAnalytics.sendHideSearch()
    }

    fun flagDragActionFinished(latLng: LatLng) {
        addPoint(latLng)
    }

    private fun addPoint(latLng: LatLng) {
        if (route.startPoint == null) {
            setStartPosition(latLng)
            logDragEvent("startFlag")
        } else {
            setFinishPosition(latLng)
            logDragEvent("finishFlag")
        }
    }

    //TODO isolated from viewmodel
    private fun logDragEvent(flagName: String) {
        if (dragStart != 0L) {//wont start when set by address
            val dragTime = System.currentTimeMillis() - dragStart
            mapAnalytics.sendDragDurationEvent(flagName, dragTime)
            dragStart = 0L
        }
    }

    fun dragStarted() {
        dragStart = System.currentTimeMillis()
    }

    fun searchAddress(string: String) {
        try {
            mapAnalytics.sendSearchAddress()
            geoCoderBusiness.getPositionFromFirst(string)?.let {
                addPoint(it)
            } ?: postError(Error.ADDRESS_NOT_FOUND)
        } catch (e: IOException) {
            handleConnectionError(e)
        }
    }

    fun requestClear() {
        mapAnalytics.sendClearRouteEvent()
        actionRequest.postValue(ClearActionRequest(this))
    }

    fun actionRequestAccepted(actionRequest: ActionRequest) {
        actionRequest.execute()
        this.actionRequest.value = null
    }

    fun actionRequestDismissed() {
        actionRequest.value = null
    }

    fun setSelectedDay(index: Int) {
        val day = Day.getByIndex(index)
        if (day != getSelectedDay()) {
            preferencesBusiness.setSelectedDay(day)
            weatherPointsAdapter.refreshRoute(getSelectedDayTime(), route)
            mightShowRateMeDialog()
            selectedDayTime.postValue(getSelectedDayTime())
        }
    }

    private fun mightShowRateMeDialog() {
        val rateMeActionRequest = RateMeActionRequest(getApplication(), mapAnalytics)
        if (rateMeActionRequest.isTimeToDisplay(mapTutorialScript, lastRunRepository, preferencesBusiness.getDaySelectionCount())) {
            actionRequest.postValue(rateMeActionRequest)
            lastRunRepository.setRun(LastRunKey.RATE_DIALOG.key)
            mapAnalytics.sendRateDialogShown()
        }
    }

    private fun getSelectedDay() = preferencesBusiness.getSelectedDay()

    fun setSelectedTime(time: Time) {
        if (time != selectedTime) {
            selectedTime = time
            weatherPointsAdapter.refreshRoute(getSelectedDayTime(), route)
            selectedDayTime.postValue(getSelectedDayTime())//TODO duplicated code with selected day
            mapAnalytics.sendTimeSelectionChanged(time)
        }
    }

    private fun getSelectedDayTime() = DayTime(getSelectedDay(), selectedTime)
}
