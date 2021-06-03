package pozzo.apps.travelweather.core.action

import android.content.Context
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import pozzo.apps.travelweather.analytics.MapAnalytics
import pozzo.apps.travelweather.common.Util
import pozzo.apps.travelweather.core.LastRunRepository
import pozzo.apps.travelweather.map.overlay.MapTutorialScript

class RateMeActionRequestTest {
    private val util: Util = mock()
    private val context: Context = mock {
        on { getString(any()) } doReturn "fakeString"
    }
    private val mapAnalytics: MapAnalytics = mock()
    private val rateMeActionRequest = RateMeActionRequest(context, mapAnalytics)

    @Before fun setup() {
        Util.instance = util
    }

    @Test fun assertBound() {
        rateMeActionRequest.execute()

        verify(util).openUrl(any(), any())
    }

    @Test fun shouldBeTimeToDisplay() {
        val mapTutorialScript: MapTutorialScript = mock {
            on { hasPlayed(any()) } doReturn true
        }
        val lastRunRepository: LastRunRepository = mock()

        val isTimeToDisplay = rateMeActionRequest.isTimeToDisplay(mapTutorialScript, lastRunRepository, 8)

        assertTrue(isTimeToDisplay)
    }

    @Test fun shouldNotBeTimeToDisplay() {
        val mapTutorialScript: MapTutorialScript = mock {
            on { hasPlayed(any()) } doReturn true
        }
        val lastRunRepository: LastRunRepository = mock {
            on { hasRun(any()) } doReturn true
        }

        assertFalse(rateMeActionRequest.isTimeToDisplay(mapTutorialScript, mock(), 1))
        assertFalse(rateMeActionRequest.isTimeToDisplay(mapTutorialScript, lastRunRepository, 99))
        assertFalse(rateMeActionRequest.isTimeToDisplay(mock(), mock(), 99))
        assertFalse(rateMeActionRequest.isTimeToDisplay(mock(), lastRunRepository, 99))
    }
}
