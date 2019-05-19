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
        mapAnalytics = MapAnalytics(firebaseAnalytics, emptyList())
    }

    @Test fun assertFirebaseIsBeingCalled() {
        mapAnalytics.sendFirebaseUserRequestedCurrentLocationEvent()
        mapAnalytics.sendClearRouteEvent()
        mapAnalytics.sendDragDurationEvent("untiTest", 1L)
        mapAnalytics.sendDragDurationEvent("untiTest", 500L)
        mapAnalytics.sendDragDurationEvent("untiTest", 1000L)
        mapAnalytics.sendDragDurationEvent("untiTest", 5000L)
        mapAnalytics.sendDaySelectionChanged(Day.TODAY)
        mapAnalytics.sendErrorMessage(Error.CANT_FIND_ROUTE)
        mapAnalytics.sendShowSearch()
        mapAnalytics.sendSearchAddress()
        mapAnalytics.sendRateDialogShown()
        mapAnalytics.sendIWantToRate()
        mapAnalytics.sendEmptyForecastCountByRoute()
        mapAnalytics.sendSingleForecastCountByRoute(0)
        mapAnalytics.sendForecastCountByRoute(0, 0)

        Mockito.verify(firebaseAnalytics, Mockito.times(15)).logEvent(any(), any())
    }
}
