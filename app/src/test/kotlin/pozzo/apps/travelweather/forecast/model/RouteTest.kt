package pozzo.apps.travelweather.forecast.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito
import pozzo.apps.travelweather.forecast.model.point.FinishPoint
import pozzo.apps.travelweather.forecast.model.point.StartPoint

class RouteTest {

    @Test fun shouldReturnTrueForHasStartAndFinish() {
        val route = Route(startPoint = Mockito.mock(StartPoint::class.java),
                finishPoint = Mockito.mock(FinishPoint::class.java))

        assertTrue(route.hasStartAndFinish())
    }

    @Test fun shouldReturnFalseForHasStartAndFinish() {
        assertFalse(Route().hasStartAndFinish())
    }
}
