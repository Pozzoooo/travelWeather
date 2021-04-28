package pozzo.apps.travelweather.map.parser

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import pozzo.apps.travelweather.core.CoroutineSettings
import pozzo.apps.travelweather.forecast.model.DayTime
import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.forecast.model.point.WeatherPoint
import java.util.*
import java.util.concurrent.CancellationException
import kotlin.collections.ArrayList

class WeatherPointsAdapter(
        private val weatherPointsData: MutableLiveData<Channel<WeatherPoint>>,
        private val scope: CoroutineScope) {

    companion object {
        private const val TWO_HOURS = 2L * 60L * 60L * 1000L
    }

    private var cachedWeatherPoints: ArrayList<WeatherPoint>? = null
    private var job: Job? = null
    private lateinit var date: Calendar

    private fun calculateGap(route: Route): Long {
        val distance = route.direction?.duration?.getMillis() ?: return TWO_HOURS
        return distance / route.weatherLocationCount
    }

    fun updateWeatherPoints(dayTime: DayTime, route: Route) {
        job?.cancel()
        job = scope.launch(CoroutineSettings.background) {
            val weatherPoints = ArrayList<WeatherPoint>()
            val weatherPointsChannel = setup(dayTime)

            try {
                weatherPointsData.postValue(weatherPointsChannel)
                for (it in route.weatherPoints) {
                    inLoop(it, route, weatherPointsChannel)
                    weatherPoints.add(it)
                }
                cachedWeatherPoints = weatherPoints
            } catch (e: CancellationException) {
                route.weatherPoints.cancel()
            } finally {
                cleanup(weatherPointsChannel)
            }
        }
    }

    private fun setup(dayTime: DayTime): Channel<WeatherPoint> {
        date = dayTime.toCalendar()
        return Channel(1)
    }

    private suspend fun inLoop(weatherPoint: WeatherPoint, route: Route,
                               weatherPointsChannel: Channel<WeatherPoint>) {
        weatherPoint.date = date
        date = GregorianCalendar().apply {
            timeInMillis = date.timeInMillis + calculateGap(route)
        }
        weatherPointsChannel.send(weatherPoint)
    }

    private fun cleanup(weatherPointsChannel: Channel<WeatherPoint>) {
        weatherPointsChannel.close()
    }

    fun refreshRoute(dayTime: DayTime, route: Route) {
        scope.launch(CoroutineSettings.background) {
            if (job?.isActive == true) job?.join()
            val cachedWeatherPoints = cachedWeatherPoints ?: return@launch

            job = scope.launch(CoroutineSettings.background) {
                val weatherPointsChannel = setup(dayTime)

                try {
                    weatherPointsData.postValue(weatherPointsChannel)
                    cachedWeatherPoints.forEach {
                        inLoop(it, route, weatherPointsChannel)
                    }
                } finally {
                    cleanup(weatherPointsChannel)
                }
            }
        }
    }
}
