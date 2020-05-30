package pozzo.apps.travelweather.map.parser

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import pozzo.apps.travelweather.core.CoroutineSettings
import pozzo.apps.travelweather.forecast.model.DayTime
import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.forecast.model.point.WeatherPoint
import java.util.*
import kotlin.collections.ArrayList

class WeatherPointsAdapter(private val weatherPointsData: MutableLiveData<Channel<WeatherPoint>>) {
    companion object {
        private const val TWO_HOURS = 2L * 60L * 60L * 1000L
    }

    private var cachedWeatherPoints: ArrayList<WeatherPoint>? = null
    private lateinit var weatherPointsChannel: Channel<WeatherPoint>
    private lateinit var date: Calendar

    fun updateWeatherPoints(dayTime: DayTime, route: Route) {
        GlobalScope.launch(CoroutineSettings.background) {
            val weatherPoints = ArrayList<WeatherPoint>()
            setup(dayTime)

            for (it in route.weatherPoints) {
                inLoop(it)
                weatherPoints.add(it)
            }
            cleanup()
            cachedWeatherPoints = weatherPoints
        }
    }

    private fun setup(dayTime: DayTime) {
        weatherPointsChannel = Channel(1)
        weatherPointsData.postValue(weatherPointsChannel)
        date = dayTime.toCalendar()
    }

    private suspend fun inLoop(weatherPoint: WeatherPoint) {
        weatherPoint.date = date
        date = GregorianCalendar().apply {
            timeInMillis = date.timeInMillis + TWO_HOURS
        }
        weatherPointsChannel.send(weatherPoint)
    }

    private fun cleanup() {
        weatherPointsChannel.close()
    }

    fun refreshRoute(dayTime: DayTime) {
        if (cachedWeatherPoints == null) return

        GlobalScope.launch(CoroutineSettings.background) {
            setup(dayTime)

            for (it in cachedWeatherPoints!!) {
                inLoop(it)
            }
            cleanup()
        }
    }
}
