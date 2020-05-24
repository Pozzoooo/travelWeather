package pozzo.apps.travelweather.map.parser

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import pozzo.apps.travelweather.core.CoroutineSettings
import pozzo.apps.travelweather.forecast.model.Day
import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.forecast.model.point.WeatherPoint
import java.util.*
import kotlin.collections.ArrayList

//TODO write specific tests
class WeatherPointsAdapter(private val weatherPointsData: MutableLiveData<Channel<WeatherPoint>>) {
    companion object {
        private const val TWO_HOURS = 2L * 60L * 60L * 1000L
    }

    private lateinit var cachedWeatherPoints: ArrayList<WeatherPoint>
    private lateinit var weatherPointsChannel: Channel<WeatherPoint>
    private lateinit var date: Calendar

    fun updateWeatherPoints(day: Day, route: Route) {
        GlobalScope.launch(CoroutineSettings.background) {
            cachedWeatherPoints = ArrayList()
            setup(day)

            for (it in route.weatherPoints) {
                inLoop(it)
                cachedWeatherPoints.add(it)
            }
            cleanup()
        }
    }

    private fun setup(day: Day) {
        weatherPointsChannel = Channel(1)
        weatherPointsData.postValue(weatherPointsChannel)
        date = day.toCalendar()
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

    fun refreshRoute(day: Day) {
        GlobalScope.launch(CoroutineSettings.background) {
            setup(day)

            for (it in cachedWeatherPoints) {
                inLoop(it)
            }
            cleanup()
        }
    }
}
