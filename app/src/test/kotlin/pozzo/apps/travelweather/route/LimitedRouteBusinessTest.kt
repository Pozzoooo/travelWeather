package pozzo.apps.travelweather.route

import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import pozzo.apps.travelweather.common.business.PreferencesBusiness
import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.forecast.model.point.FinishPoint
import pozzo.apps.travelweather.forecast.model.point.StartPoint

class LimitedRouteBusinessTest {
    private val startPoint = StartPoint(LatLng(.0, .0))
    private val finishPoint = FinishPoint(LatLng(.0, .0))
    private val route = Route(startPoint = startPoint, finishPoint = finishPoint)

    private val preferencesBusiness: PreferencesBusiness = mock()
    private val unlimitedRouteBusiness: UnlimitedRouteBusiness = mock {
        on { createRoute(route) } doReturn route
    }

    private val business = LimitedRouteBusiness(unlimitedRouteBusiness, preferencesBusiness)

    @Test fun assertMaxRequestIsReasonable() {
        assertTrue("way too low, user needs to use the system",
                business.getMaxRequest() > 99)
    }

    @Test fun assertInitialIsMax() {
        assertEquals(business.getMaxRequest(), business.getAvailableRequests())
    }

    @Test fun shouldCreateRoute() {
        val actualRoute = business.createRoute(route)

        assertEquals(route, actualRoute)
    }

    @Test(expected = RequestLimitReached::class) fun shouldBlockWhenLimitReached() {
        mockLimitReached()

        business.createRoute(route)
    }

    private fun mockLimitReached() {
        whenever(preferencesBusiness.getUsedRequestCount())
                .thenReturn(business.getMaxRequest() + 1)
    }

    @Test fun shouldCreateRouteOnTheDayAfterReachingLimit() {
        whenever(preferencesBusiness.getLastRemainingRequestReset())
                .thenReturn(System.currentTimeMillis() - 24L * 60L * 60L * 1000L - 1)

        business.createRoute(route)

        verify(preferencesBusiness).resetUsedRequestCount()
    }
}
