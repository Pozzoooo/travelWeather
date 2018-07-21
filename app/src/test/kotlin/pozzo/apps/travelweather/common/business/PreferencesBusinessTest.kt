package pozzo.apps.travelweather.common.business

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
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
        val editor = Mockito.mock(SharedPreferences.Editor::class.java)
        whenever(preferences.edit()).thenReturn(editor)

        preferencesBusiness.setSelectedDay(Day.TOMORROW)

        verify(editor).putInt("selectedDay", Day.TOMORROW.index)
        verify(editor).apply()
    }

    @Test fun assertReadinSelctionCount() {
        whenever(preferences.getInt("daySelectionCount", 0)).thenReturn(100)

        val count = preferencesBusiness.getDaySelectionCount()

        Assert.assertEquals(100, count)
    }
}
