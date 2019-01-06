package pozzo.apps.travelweather.forecast.model

import org.junit.Assert.assertEquals
import org.junit.Test
import pozzo.apps.travelweather.App
import pozzo.apps.travelweather.core.TestInjector
import java.util.*

class DayTest {

    @Test fun assertItIsGettingProperDayByIndex() {
        assertEquals(Day.TODAY, Day.getByIndex(0))
        assertEquals(Day.TOMORROW, Day.getByIndex(1))
        assertEquals(Day.AFTER_TOMORROW, Day.getByIndex(2))
        assertEquals(Day.TODAY, Day.getByIndex(-1))
    }

    @Test fun assertDayTitles() {
        val appComponent = TestInjector.getAppComponent()
        App.setComponent(appComponent.build())

        Locale.setDefault(Locale.US)

        assertEquals("mockString", Day.TODAY.toString())
        assertEquals("Tuesday", Day.IDX_3.toString())
//        assertEquals("Dec 1, 1990", Day.IDX_7.toString())
    }
}
