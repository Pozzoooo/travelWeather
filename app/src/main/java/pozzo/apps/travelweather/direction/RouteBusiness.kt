package pozzo.apps.travelweather.direction

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.analytics.MapAnalytics
import pozzo.apps.travelweather.forecast.ForecastBusiness
import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.forecast.model.Weather
import pozzo.apps.travelweather.forecast.model.point.FinishPoint
import pozzo.apps.travelweather.forecast.model.point.MapPoint
import pozzo.apps.travelweather.forecast.model.point.StartPoint
import pozzo.apps.travelweather.location.LocationBusiness
import pozzo.apps.travelweather.map.parser.WeatherToMapPointParser
import java.io.IOException

class RouteBusiness(private val mapAnalytics: MapAnalytics) {
    //todo seems like its time to bring dagger to my project
    private val locationBusiness = LocationBusiness()
    private val directionLineBusiness = DirectionLineBusiness()
    private val forecastBusiness = ForecastBusiness()

    private val directionWeatherFilter = DirectionWeatherFilter()
    private val weatherToMapPointParser = WeatherToMapPointParser()

    @Throws(DirectionNotFoundException::class, IOException::class)
    fun createRoute(startPoint: StartPoint?, finishPoint: FinishPoint?): Route {
        if (startPoint == null || finishPoint == null) return Route(startPoint = startPoint, finishPoint = finishPoint)

        val direction = locationBusiness.getDirections(startPoint.position, finishPoint.position)
        if (direction?.isEmpty() != false) throw DirectionNotFoundException()

        val directionLine = directionLineBusiness.createDirectionLine(direction)
        val mapPoints = parseToMapPoints(directionWeatherFilter.getWeatherPointsLocations(direction))
        return Route(startPoint = startPoint, finishPoint = finishPoint, polyline = directionLine, mapPoints = mapPoints)
    }

    //todo is it this classes responsability?
    private fun parseToMapPoints(weatherPoints: List<LatLng>) : List<MapPoint> {
        val weathers = requestWeathersFor(weatherPoints)
        return weatherToMapPointParser.parse(weathers)
    }

    private fun requestWeathersFor(weatherPoints: List<LatLng>) : List<Weather> {
        val weathers = forecastBusiness.from(weatherPoints)
        if (weathers.size != weatherPoints.size) mapAnalytics.sendWeatherMiss(weatherPoints.size, weathers.size)
        return weathers
    }
}
