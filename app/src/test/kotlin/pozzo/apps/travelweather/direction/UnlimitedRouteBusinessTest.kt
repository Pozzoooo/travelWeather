package pozzo.apps.travelweather.direction

import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import pozzo.apps.travelweather.common.android.BitmapCreator
import pozzo.apps.travelweather.common.android.BitmapCreatorTest
import pozzo.apps.travelweather.direction.google.GoogleDirection
import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.forecast.model.point.FinishPoint
import pozzo.apps.travelweather.forecast.model.point.StartPoint
import pozzo.apps.travelweather.map.parser.MapPointCreator
import pozzo.apps.travelweather.route.UnlimitedRouteBusiness

class UnlimitedRouteBusinessTest {
    private lateinit var unlimitedRouteBusiness: UnlimitedRouteBusiness

    @Mock private lateinit var googleDirection: GoogleDirection
    @Mock private lateinit var directionLineBusiness: DirectionLineBusiness
    @Mock private lateinit var mapPointCreator: MapPointCreator
    @Mock private lateinit var directionWeatherFilter: DirectionWeatherFilter

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)
        BitmapCreator.setInstance(BitmapCreatorTest())

        unlimitedRouteBusiness = UnlimitedRouteBusiness(directionLineBusiness, mapPointCreator, googleDirection, directionWeatherFilter)
    }

    @Test fun incompleteRouteShouldNotCreateRoute() {
        val emptyRoute = Route()
        assertFalse(unlimitedRouteBusiness.createRoute(emptyRoute).isComplete())
        val endlessRoute = Route(startPoint = StartPoint(LatLng(0.0, 0.0)))
        assertFalse(unlimitedRouteBusiness.createRoute(endlessRoute).isComplete())
        val startLessRoute = Route(finishPoint = FinishPoint(LatLng(0.0, 0.0)))
        assertFalse(unlimitedRouteBusiness.createRoute(startLessRoute).isComplete())
    }

    @Test(expected = DirectionNotFoundException::class) fun shouldThrowWhenDirectionIsNotFound() {
        val startPoint = StartPoint(LatLng(0.0, 0.0))
        val finishPoint = FinishPoint(LatLng(0.0, 0.0))
        val route = Route(startPoint = startPoint, finishPoint = finishPoint)

        try {
            unlimitedRouteBusiness.createRoute(route)
            fail()
        } catch (e : DirectionNotFoundException) { /*success*/ }

        whenever(googleDirection.getDirection(startPoint.position, finishPoint.position))
                .thenReturn(Direction(emptyList(), null, null))
        unlimitedRouteBusiness.createRoute(route)
    }

    @Test fun shouldCreateAProperRoute() {
        val startPoint = StartPoint(LatLng(0.0, 0.0))
        val finishPoint = FinishPoint(LatLng(0.0, 0.0))
        var route = Route(startPoint = startPoint, finishPoint = finishPoint)

        whenever(googleDirection.getDirection(listOf(startPoint.position, finishPoint.position)))
                .thenReturn(Direction(listOf(LatLng(0.0, 1.0), LatLng(2.0, 3.0)), null, null))

        route = unlimitedRouteBusiness.createRoute(route)

        assertEquals(startPoint, route.startPoint)
        assertEquals(finishPoint, route.finishPoint)
    }
}
