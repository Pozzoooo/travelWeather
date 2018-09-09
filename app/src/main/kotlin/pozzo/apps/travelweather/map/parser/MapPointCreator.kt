package pozzo.apps.travelweather.map.parser

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.launch
import pozzo.apps.travelweather.core.CoroutineSettings.background
import pozzo.apps.travelweather.direction.DirectionWeatherFilter
import pozzo.apps.travelweather.forecast.ForecastBusiness
import pozzo.apps.travelweather.forecast.model.point.MapPoint

class MapPointCreator(
        private val forecastBusiness: ForecastBusiness,
        private val directionWeatherFilter: DirectionWeatherFilter,
        private val weatherToMapPointParser: WeatherToMapPointParser) {

    fun createMapPointsAsync(direction: List<LatLng>) : Channel<MapPoint> {
        val mapPoints = Channel<MapPoint>()
        launch(background) {
            directionWeatherFilter.getWeatherPointsLocations(direction).asSequence()
                    .mapNotNull(forecastBusiness::forecast)
                    .mapNotNull(weatherToMapPointParser::parse)
                    .forEach { mapPoints.send(it) }
            mapPoints.close()
        }
        return mapPoints
    }
}
