package pozzo.apps.travelweather.route

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.direction.DirectionLineBusiness
import pozzo.apps.travelweather.direction.DirectionNotFoundException
import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.forecast.model.point.FinishPoint
import pozzo.apps.travelweather.forecast.model.point.StartPoint
import pozzo.apps.travelweather.direction.google.GoogleDirection
import pozzo.apps.travelweather.map.parser.MapPointCreator
import java.io.IOException

class UnlimitedRouteBusiness(
        private val directionLineBusiness: DirectionLineBusiness,
        private val mapPointCreator: MapPointCreator,
        private val googleDirection: GoogleDirection) : RouteBusiness {

    @Throws(DirectionNotFoundException::class, IOException::class)
    override fun createRoute(startPoint: StartPoint?, finishPoint: FinishPoint?): Route {
        if (startPoint == null || finishPoint == null) return Route(startPoint = startPoint, finishPoint = finishPoint)

        val direction = getDirections(startPoint.position, finishPoint.position)
        if (direction?.isEmpty() != false) throw DirectionNotFoundException()

        val directionLine = directionLineBusiness.createDirectionLine(direction)
        val mapPoints = mapPointCreator.createMapPointsAsync(direction)

        return Route(startPoint = startPoint, finishPoint = finishPoint, polyline = directionLine, mapPoints = mapPoints)
    }

    private fun getDirections(startPosition: LatLng, finishPosition: LatLng): List<LatLng>? {
        return googleDirection.getDirection(startPosition, finishPosition)
    }
}
