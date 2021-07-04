package pozzo.apps.travelweather.route

import pozzo.apps.travelweather.direction.DirectionLineBusiness
import pozzo.apps.travelweather.direction.DirectionNotFoundException
import pozzo.apps.travelweather.direction.DirectionWeatherFilter
import pozzo.apps.travelweather.direction.google.GoogleDirection
import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.map.parser.MapPointCreator

class UnlimitedRouteBusiness(
        private val directionLineBusiness: DirectionLineBusiness,
        private val mapPointCreator: MapPointCreator,
        private val googleDirection: GoogleDirection,
        private val directionWeatherFilter: DirectionWeatherFilter) : RouteBusiness {

    override fun createRoute(route: Route): Route {
        if (!route.isComplete()) return route

        val direction = googleDirection.getDirection(route.getAllWaypoints())
        if (direction?.isEmpty() != false) throw DirectionNotFoundException()

        val directionLine = directionLineBusiness.createDirectionLine(direction.steps)
        val weatherPointLocation = directionWeatherFilter.getWeatherPointsLocations(direction.steps)
        val mapPoints = mapPointCreator.createMapPointsAsync(weatherPointLocation)

        return Route(baseRoute = route, polyline = directionLine, weatherPoints = mapPoints,
                weatherLocationCount = weatherPointLocation.size, direction = direction)
    }
}
