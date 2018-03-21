package pozzo.apps.travelweather.map.viewmodel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import pozzo.apps.travelweather.core.BaseViewModel
import pozzo.apps.travelweather.forecast.ForecastHelper
import pozzo.apps.travelweather.location.LocationBusiness
import pozzo.apps.travelweather.location.LocationLiveData
import java.util.concurrent.Executors

class MapViewModel(application: Application) : BaseViewModel(application) {
    private val locationBusiness: LocationBusiness = LocationBusiness()
    private val routeExecutor = Executors.newSingleThreadExecutor()

    val startPosition = MutableLiveData<LatLng?>()
    val finishPosition = MutableLiveData<LatLng?>()
    val isShowingProgress = MutableLiveData<Boolean>()
    val directionLine = MutableLiveData<PolylineOptions>()
    val weatherPoints = MutableLiveData<List<LatLng>>()

    init {
        isShowingProgress.value = false
    }

    fun currentLocationFabClick() {
        finishPosition.postValue(null)
        startPosition.postValue(getCurrentLocation())
    }

    fun getLiveLocation(): LocationLiveData {
        return LocationLiveData.get(getApplication())
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
                setDirectionWeatherPoints(direction)
            }
            hideProgress()
        })
    }

    private fun setDirectionWeatherPoints(direction: List<LatLng>) {
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
        weatherPoints.postValue(filteredPoints)
    }

    private fun setDirectionLine(direction: List<LatLng>) {
        val rectLine = PolylineOptions().width(7F).color(Color.BLUE)
        direction.forEach {
            rectLine.add(it)
        }
        this.directionLine.postValue(rectLine)
    }

    fun setFinishPosition(finishPosition: LatLng?) {
        this.finishPosition.postValue(finishPosition)
    }

    fun setStartPosition(startPosition: LatLng?) {
        this.startPosition.postValue(startPosition)
    }

    //todo remove it when refacted enough
    fun showProgress() {
        isShowingProgress.postValue(true)
    }
    fun hideProgress() {
        isShowingProgress.postValue(false)
    }
}
