package pozzo.apps.travelweather.map.parser

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pozzo.apps.travelweather.core.CoroutineSettings.background
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.forecast.ForecastBusiness
import pozzo.apps.travelweather.forecast.model.point.WeatherPoint
import java.util.concurrent.CancellationException

class MapPointCreator(
        private val forecastBusiness: ForecastBusiness,
        private val weatherToMapPointParser: WeatherToMapPointParser) {

    fun createMapPointsAsync(weatherPointLocation: List<LatLng>) : Channel<WeatherPoint> {
        val mapPoints = Channel<WeatherPoint>()
        GlobalScope.launch(background) {
            weatherPointLocation.asSequence()
                    .mapIndexed(::sparseCalls)
                    .mapNotNull(forecastBusiness::forecast)
                    .mapNotNull(weatherToMapPointParser::parse)
                    .forEach {
                        try {
                            mapPoints.send(it)
                        } catch (e: CancellationException) {
                            Bug.get().logException(e)//Just to find out how frequent it is
                        }
                    }
            mapPoints.close()
        }
        return mapPoints
    }

    private fun sparseCalls(sparseBy: Int, latLng: LatLng): LatLng {
        runBlocking {
            delay(sparseBy * 100L)
        }
        return latLng
    }
}
