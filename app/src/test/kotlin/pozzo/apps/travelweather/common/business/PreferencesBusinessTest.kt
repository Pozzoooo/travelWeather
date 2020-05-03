package pozzo.apps.travelweather.common.business

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import pozzo.apps.travelweather.core.TestInjector
import pozzo.apps.travelweather.forecast.model.Day

class PreferencesBusinessTest {
    @Mock private lateinit var preferences: SharedPreferences
    private lateinit var preferencesBusiness: PreferencesBusiness

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)
        val appComponent = TestInjector.getAppComponent().build()
        preferencesBusiness = PreferencesBusiness(preferences, appComponent.mapAnalytics())
    }

    @Test fun assertGettingDay() {
        whenever(preferences.getInt(eq("selectedDay"), any())).thenReturn(2)
        val selectedDat = preferencesBusiness.getSelectedDay()
        assertEquals(Day.AFTER_TOMORROW, selectedDat)
    }

    @Test fun assertSavingDay() {
        val editor = mockEditor()

        preferencesBusiness.setSelectedDay(Day.TOMORROW)

        verify(editor).putInt("selectedDay", Day.TOMORROW.index)
        verify(editor).apply()
    }

    private fun mockEditor(): SharedPreferences.Editor {
        val editor = mock(SharedPreferences.Editor::class.java)
        whenever(editor.putInt(any(), any())).thenReturn(editor)
        whenever(editor.putLong(any(), any())).thenReturn(editor)

        whenever(preferences.edit()).thenReturn(editor)
        return editor
    }

    @Test fun assertPreferencesAreBeingRead() {
        whenever(preferences.getInt("daySelectionCount", 0)).thenReturn(100)
        whenever(preferences.getInt("usedRequestCount", 0)).thenReturn(7)
        whenever(preferences.getLong("lastRemainingRequestReset", 0)).thenReturn(900L)

        assertEquals(100, preferencesBusiness.getDaySelectionCount())
        assertEquals(7, preferencesBusiness.getUsedRequestCount())
        assertEquals(900L, preferencesBusiness.getLastRemainingRequestReset())
    }

    @Test fun assertUsedRequestCountAddition() {
        whenever(preferences.getInt("usedRequestCount", 0)).thenReturn(7)
        val editor = mockEditor()

        preferencesBusiness.addUsedRequestCount(20)

        verify(editor).putInt("usedRequestCount", 27)
        verify(editor).apply()
    }

    @Test fun assertUsedRequestCountReset() {
        val editor = mockEditor()

        preferencesBusiness.resetUsedRequestCount()

        verify(editor).putInt("usedRequestCount", 0)
        verify(editor).apply()
    }
}
