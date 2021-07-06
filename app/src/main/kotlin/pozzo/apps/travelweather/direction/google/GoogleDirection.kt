package pozzo.apps.travelweather.direction.google

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.direction.Direction
import pozzo.apps.travelweather.direction.Distance
import pozzo.apps.travelweather.direction.Duration

class GoogleDirection(private val requester: GoogleDirectionRequester,
                      private val parser: GoogleResponseParser,
                      private val polylineDecoder: PolylineDecoder) {

    fun getDirection(start: LatLng, end: LatLng): Direction? {
        return getDirection(listOf(start, end))
    }

    fun getDirection(waypoints: List<LatLng>): Direction? {
        val response = requester.request(waypoints)
        val parsedResponse = response?.let { parser.parse(it) }
//        val firstLeg = parsedResponse?.routes?.firstOrNull()?.legs?.firstOrNull() ?: return null
//        return parseToDirection(firstLeg)
        val route = parsedResponse?.routes?.firstOrNull() ?: return null
        return mergeLegs(route)
    }

    private fun mergeLegs(route: GoogleRoutes): Direction {
        var duration = 0
        var distance = 0
        val steps = route.legs.flatMap {
            distance += it.distance?.value ?: 0//TODO do I handle it properly or remove completely?
            duration += it.duration?.value ?: 0
            it.steps
        }.flatMap {
            polylineDecoder.decode(it.polyline.points)
        }
        return Direction(steps, Duration(duration, ""), Distance(distance, ""))
    }

    private fun parseToDirection(leg: GoogleLegs): Direction {
        val steps = readAllPolylineFromSteps(leg.steps)
        val duration = leg.duration?.run { Duration(value, text) }
        val distance = leg.distance?.run { Distance(value, text) }
        return Direction(steps, duration, distance)
    }

    private fun readAllPolylineFromSteps(steps: List<GoogleSteps>): List<LatLng> {
        return steps.flatMap {
            polylineDecoder.decode(it.polyline.points)
        }
    }
}
