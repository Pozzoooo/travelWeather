package pozzo.apps.travelweather.route

import pozzo.apps.travelweather.direction.DirectionLineBusiness
import pozzo.apps.travelweather.direction.DirectionNotFoundException
import pozzo.apps.travelweather.direction.DirectionWeatherFilter
import pozzo.apps.travelweather.direction.google.GoogleDirection
import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.forecast.model.point.FinishPoint
import pozzo.apps.travelweather.forecast.model.point.StartPoint
import pozzo.apps.travelweather.map.parser.MapPointCreator

class UnlimitedRouteBusiness(
        private val directionLineBusiness: DirectionLineBusiness,
        private val mapPointCreator: MapPointCreator,
        private val googleDirection: GoogleDirection,
        private val directionWeatherFilter: DirectionWeatherFilter) : RouteBusiness {

    override fun createRoute(startPoint: StartPoint?, finishPoint: FinishPoint?): Route {
        if (startPoint == null || finishPoint == null) return Route(startPoint = startPoint, finishPoint = finishPoint)

        val direction = googleDirection.getDirection(startPoint.position, finishPoint.position)
        if (direction?.isEmpty() != false) throw DirectionNotFoundException()

        val directionLine = directionLineBusiness.createDirectionLine(direction.steps)
        val weatherPointLocation = directionWeatherFilter.getWeatherPointsLocations(direction.steps)
        val mapPoints = mapPointCreator.createMapPointsAsync(weatherPointLocation)

        return Route(startPoint = startPoint, finishPoint = finishPoint, polyline = directionLine,
                weatherPoints = mapPoints, weatherLocationCount = weatherPointLocation.size,
                direction = direction)
    }
}
