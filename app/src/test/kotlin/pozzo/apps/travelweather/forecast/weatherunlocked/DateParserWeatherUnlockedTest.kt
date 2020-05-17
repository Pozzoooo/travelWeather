package pozzo.apps.travelweather.forecast.weatherunlocked

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class DateParserWeatherUnlockedTest {
    private val parser = DateParserWeatherUnlocked()

    @Test fun assertParsing() {
        val timeframe = JsonObject().apply {
            add("date", JsonPrimitive("01/01/2019"))
            add("time", JsonPrimitive(600))
        }

        val parsed = parser.parse(timeframe)

        val expected = GregorianCalendar(2019, 0, 1, 6, 0, 0)
        assertEquals(expected.timeInMillis, parsed.timeInMillis)
    }
}
