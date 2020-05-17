package pozzo.apps.travelweather.forecast.model

import org.junit.Assert.assertEquals
import org.junit.Test
import pozzo.apps.travelweather.App
import pozzo.apps.travelweather.core.TestInjector
import java.util.*
import java.util.Calendar.DAY_OF_YEAR

class DayTest {

    @Test fun assertItIsGettingProperDayByIndex() {
        assertEquals(Day.TODAY, Day.getByIndex(0))
        assertEquals(Day.TOMORROW, Day.getByIndex(1))
        assertEquals(Day.AFTER_TOMORROW, Day.getByIndex(2))
        assertEquals(Day.DEFAULT, Day.getByIndex(-1))
    }

    @Test fun assertDayTitles() {
        val appComponent = TestInjector.getAppComponent()
        App.setComponent(appComponent.build())

        Locale.setDefault(Locale.US)

        assertEquals("mockString", Day.TODAY.toString())
        assertEquals("Tuesday", Day.IDX_3.toString())
        assertEquals("Dec 1, 1990", Day.IDX_7.toString())
    }

    @Test fun assertCalendarConversion() {
        val calendar = GregorianCalendar.getInstance()
        assertEquals(calendar.get(DAY_OF_YEAR), Day.TODAY.toCalendar().get(DAY_OF_YEAR))
        calendar.roll(DAY_OF_YEAR, 1)
        assertEquals(calendar.get(DAY_OF_YEAR), Day.TOMORROW.toCalendar().get(DAY_OF_YEAR))
        calendar.roll(DAY_OF_YEAR, 9)
        assertEquals(calendar.get(DAY_OF_YEAR), Day.IDX_10.toCalendar().get(DAY_OF_YEAR))
    }
}
