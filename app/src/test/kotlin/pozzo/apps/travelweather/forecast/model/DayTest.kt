package pozzo.apps.travelweather.forecast.model

import org.junit.Assert.*
import org.junit.Test

class DayTest {

    @Test fun assertItIsGettingProperDayByIndex() {
        assertEquals(Day.TODAY, Day.getByIndex(0))
        assertEquals(Day.TOMORROW, Day.getByIndex(1))
        assertEquals(Day.AFTER_TOMORROW, Day.getByIndex(2))
    }
}
