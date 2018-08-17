package pozzo.apps.travelweather.direction

import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.forecast.model.point.FinishPoint
import pozzo.apps.travelweather.forecast.model.point.StartPoint
import pozzo.apps.travelweather.location.LocationBusiness
import pozzo.apps.travelweather.map.parser.MapPointCreator
import java.io.IOException

//todo how does that @Inject constructor works? Can it make this class look a bit simpler?
class DirectionBusiness(
        private val locationBusiness: LocationBusiness,
        private val directionLineBusiness: DirectionLineBusiness,
        private val mapPointCreator: MapPointCreator) {

    @Throws(DirectionNotFoundException::class, IOException::class)
    fun createRoute(startPoint: StartPoint?, finishPoint: FinishPoint?): Route {
        if (startPoint == null || finishPoint == null) return Route(startPoint = startPoint, finishPoint = finishPoint)

        val direction = locationBusiness.getDirections(startPoint.position, finishPoint.position)
        if (direction?.isEmpty() != false) throw DirectionNotFoundException()

        val directionLine = directionLineBusiness.createDirectionLine(direction)
        val mapPoints = mapPointCreator.createMapPointsAsync(direction)

        return Route(startPoint = startPoint, finishPoint = finishPoint, polyline = directionLine, mapPoints = mapPoints)
    }
}
