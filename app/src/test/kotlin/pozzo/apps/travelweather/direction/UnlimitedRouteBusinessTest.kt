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
        assertFalse(unlimitedRouteBusiness.createRoute(null, null).isComplete())
        assertFalse(unlimitedRouteBusiness.createRoute(StartPoint(LatLng(0.0, 0.0)), null).isComplete())
        assertFalse(unlimitedRouteBusiness.createRoute(null, FinishPoint(LatLng(0.0, 0.0))).isComplete())
    }

    @Test(expected = DirectionNotFoundException::class) fun shouldThrowWhenDirectionIsNotFound() {
        val startPoint = StartPoint(LatLng(0.0, 0.0))
        val finishPoint = FinishPoint(LatLng(0.0, 0.0))

        try {
            unlimitedRouteBusiness.createRoute(startPoint, finishPoint)
            fail()
        } catch (e : DirectionNotFoundException) { /*success*/ }

        whenever(googleDirection.getDirection(startPoint.position, finishPoint.position))
                .thenReturn(Direction(emptyList(), null, null))
        unlimitedRouteBusiness.createRoute(startPoint, finishPoint)
    }

    @Test fun shouldCreateAProperRoute() {
        val startPoint = StartPoint(LatLng(0.0, 0.0))
        val finishPoint = FinishPoint(LatLng(0.0, 0.0))

        whenever(googleDirection.getDirection(startPoint.position, finishPoint.position))
                .thenReturn(Direction(listOf(LatLng(0.0, 1.0), LatLng(2.0, 3.0)), null, null))

        val route = unlimitedRouteBusiness.createRoute(startPoint, finishPoint)

        assertEquals(startPoint, route.startPoint)
        assertEquals(finishPoint, route.finishPoint)
    }
}
