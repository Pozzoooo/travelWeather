package pozzo.apps.travelweather.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pozzo.apps.travelweather.core.Error
import pozzo.apps.travelweather.forecast.model.Day

@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
class MapAnalyticsTest {
    @Mock private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var mapAnalytics: MapAnalytics

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)
        mapAnalytics = MapAnalytics(firebaseAnalytics)
    }

    @Test fun assertFirebaseIsBeingCalled() {
        mapAnalytics.sendFirebaseUserRequestedCurrentLocationEvent()
        mapAnalytics.sendClearRouteEvent()
        mapAnalytics.sendDragDurationEvent("unitTest", 1L)
        mapAnalytics.sendDragDurationEvent("unitTest", 500L)
        mapAnalytics.sendDragDurationEvent("unitTest", 1000L)
        mapAnalytics.sendDragDurationEvent("unitTest", 5000L)
        mapAnalytics.sendDaySelectionChanged(Day.TODAY)
        mapAnalytics.sendErrorMessage(Error.CANT_FIND_ROUTE)
        mapAnalytics.sendShowSearch()
        mapAnalytics.sendSearchAddress()
        mapAnalytics.sendRateDialogShown()
        mapAnalytics.sendIWantToRate()
        mapAnalytics.sendEmptyForecastCountByRoute()
        mapAnalytics.sendSingleForecastCountByRoute(0)
        mapAnalytics.sendForecastCountByRoute(0, 0)
        mapAnalytics.sendKnownException("exc", "it's quite bad")

        Mockito.verify(firebaseAnalytics, Mockito.times(16)).logEvent(any(), any())
    }
}
