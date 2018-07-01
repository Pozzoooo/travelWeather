package pozzo.apps.travelweather.direction

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.launch
import pozzo.apps.travelweather.forecast.ForecastBusiness
import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.forecast.model.point.FinishPoint
import pozzo.apps.travelweather.forecast.model.point.MapPoint
import pozzo.apps.travelweather.forecast.model.point.StartPoint
import pozzo.apps.travelweather.location.LocationBusiness
import pozzo.apps.travelweather.map.parser.WeatherToMapPointParser
import java.io.IOException

class RouteBusiness(private val forecastBusiness: ForecastBusiness) {
    private val locationBusiness = LocationBusiness()
    private val directionLineBusiness = DirectionLineBusiness()

    private val directionWeatherFilter = DirectionWeatherFilter()
    private val weatherToMapPointParser = WeatherToMapPointParser()

    @Throws(DirectionNotFoundException::class, IOException::class)
    fun createRoute(startPoint: StartPoint?, finishPoint: FinishPoint?): Route {
        if (startPoint == null || finishPoint == null) return Route(startPoint = startPoint, finishPoint = finishPoint)

        val direction = locationBusiness.getDirections(startPoint.position, finishPoint.position)
        if (direction?.isEmpty() != false) throw DirectionNotFoundException()

        val directionLine = directionLineBusiness.createDirectionLine(direction)
        val mapPoints = requestForecasts(direction)

        return Route(startPoint = startPoint, finishPoint = finishPoint, polyline = directionLine, mapPoints = mapPoints)
    }

    //todo is it this classes responsability?
    private fun requestForecasts(direction: ArrayList<LatLng>) : Channel<MapPoint> {
        val mapPoints = Channel<MapPoint>()
        launch {
            directionWeatherFilter.getWeatherPointsLocations(direction).asSequence()
                    .mapNotNull(forecastBusiness::from)
                    .mapNotNull(weatherToMapPointParser::parse)
                    .forEach { mapPoints.send(it) }
            mapPoints.close()
        }
        return mapPoints
    }
}
