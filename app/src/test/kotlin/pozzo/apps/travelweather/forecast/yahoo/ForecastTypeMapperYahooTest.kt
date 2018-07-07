package pozzo.apps.travelweather.forecast.yahoo

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import pozzo.apps.travelweather.forecast.ForecastType
import pozzo.apps.travelweather.forecast.model.Forecast

class ForecastTypeMapperYahooTest {
    private lateinit var mapper: ForecastTypeMapperYahoo

    @Before fun setup() {
        mapper = ForecastTypeMapperYahoo()
    }

    @Test fun assertItsMapping() {
        val forecast = Forecast().apply { text = "Sunny" }

        assertEquals(ForecastType.SUNNY, mapper.getForecastType(forecast))
    }

    @Test fun assertItCanHandleTheUnknown() {
        val forecast = Forecast().apply { text = "notEvenTry!" }

        assertEquals(ForecastType.UNKNOWN, mapper.getForecastType(forecast))
    }

    @Test fun assertCaseInsensitive() {
        val forecast = Forecast().apply { text = "mOStly Sunny" }

        assertEquals(ForecastType.MOSTLY_SUNNY, mapper.getForecastType(forecast))
    }
}
