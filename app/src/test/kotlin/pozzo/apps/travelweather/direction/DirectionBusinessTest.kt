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
import pozzo.apps.travelweather.forecast.model.point.FinishPoint
import pozzo.apps.travelweather.forecast.model.point.StartPoint
import pozzo.apps.travelweather.location.LocationBusiness
import pozzo.apps.travelweather.map.parser.MapPointCreator

class DirectionBusinessTest {
    private lateinit var directionBusiness: DirectionBusiness

    @Mock private lateinit var locationBusiness: LocationBusiness
    @Mock private lateinit var directionLineBusiness: DirectionLineBusiness
    @Mock private lateinit var mapPointCreator: MapPointCreator

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)
        BitmapCreator.setInstance(BitmapCreatorTest())

        directionBusiness = DirectionBusiness(locationBusiness, directionLineBusiness, mapPointCreator)
    }

    @Test fun incompleteRouteShouldNotCreateRoute() {
        assertFalse(directionBusiness.createRoute(null, null).isComplete())
        assertFalse(directionBusiness.createRoute(StartPoint(LatLng(0.0, 0.0)), null).isComplete())
        assertFalse(directionBusiness.createRoute(null, FinishPoint(LatLng(0.0, 0.0))).isComplete())
    }

    @Test(expected = DirectionNotFoundException::class) fun shouldThrowWhenDirectionIsNotFound() {
        val startPoint = StartPoint(LatLng(0.0, 0.0))
        val finishPoint = FinishPoint(LatLng(0.0, 0.0))

        try {
            directionBusiness.createRoute(startPoint, finishPoint)
            fail()
        } catch (e : DirectionNotFoundException) { /*success*/ }

        whenever(locationBusiness.getDirections(startPoint.position, finishPoint.position)).thenReturn(emptyList())
        directionBusiness.createRoute(startPoint, finishPoint)
    }

    @Test fun shouldCreateAProperRoute() {
        val startPoint = StartPoint(LatLng(0.0, 0.0))
        val finishPoint = FinishPoint(LatLng(0.0, 0.0))

        whenever(locationBusiness.getDirections(startPoint.position, finishPoint.position))
                .thenReturn(listOf(LatLng(0.0, 1.0), LatLng(2.0, 3.0)))

        val route = directionBusiness.createRoute(startPoint, finishPoint)

        assertEquals(startPoint, route.startPoint)
        assertEquals(finishPoint, route.finishPoint)
    }
}
