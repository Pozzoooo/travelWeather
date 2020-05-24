package pozzo.apps.travelweather.map.parser

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import pozzo.apps.travelweather.core.CoroutineSettings
import pozzo.apps.travelweather.forecast.model.Day
import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.forecast.model.point.WeatherPoint
import java.util.ArrayList

//TODO write specific tests
class WeatherPointsAdapter(private val weatherPointsData: MutableLiveData<Channel<WeatherPoint>>) {
    private val cachedWeatherPoints = ArrayList<WeatherPoint>()

    fun updateWeatherPoints(day: Day, route: Route) {
        GlobalScope.launch(CoroutineSettings.background) {
            val weatherPointsChannel = createWeatherPointsChannel()

            for (it in route.weatherPoints) {
                it.date = day.toCalendar()
                cachedWeatherPoints.add(it)
                weatherPointsChannel.send(it)
            }
            weatherPointsChannel.close()
        }
    }

    private fun createWeatherPointsChannel(): Channel<WeatherPoint> {
        return Channel<WeatherPoint>(1).also {
            weatherPointsData.postValue(it)
        }
    }

    fun refreshRoute(day: Day) {
        GlobalScope.launch(CoroutineSettings.background) {
            val weatherPointsChannel = createWeatherPointsChannel()

            for (it in cachedWeatherPoints) {
                it.date = day.toCalendar()
                weatherPointsChannel.send(it)
            }
            weatherPointsChannel.close()
        }
    }
}
