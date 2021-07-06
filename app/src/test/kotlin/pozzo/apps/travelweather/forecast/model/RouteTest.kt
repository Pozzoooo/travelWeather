package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.*
import org.junit.Test
import pozzo.apps.travelweather.forecast.model.point.FinishPoint
import pozzo.apps.travelweather.forecast.model.point.StartPoint
import pozzo.apps.travelweather.forecast.model.point.WayPoint

class RouteTest {

    @Test fun shouldReturnTrueForHasStartAndFinish() {
        val route = Route(
                startPoint = StartPoint(LatLng(.0, .0)),
                finishPoint = FinishPoint(LatLng(.0, .0))
        )

        assertTrue(route.isComplete())
    }

    @Test fun shouldReturnFalseWhenIncomplete() {
        assertFalse(Route(startPoint = StartPoint(LatLng(.0, .0))).isComplete())
    }

    @Test fun shouldReturnFalseForHasStartAndFinish() {
        assertFalse(Route().isComplete())
    }

    @Test fun shouldReturnAllWaypoints() {
        val route = Route(
                startPoint = StartPoint(LatLng(.0, .0)),
                finishPoint = FinishPoint(LatLng(.0, .0)),
                waypoints = listOf(WayPoint(position = LatLng(.0, .0)))
        )

        val allWaypoints = route.getAllPointsPosition()

        assertEquals(3, allWaypoints.size)
        assertTrue(route.isComplete())
    }
}
