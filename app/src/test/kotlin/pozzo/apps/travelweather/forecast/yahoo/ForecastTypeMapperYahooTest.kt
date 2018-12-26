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
        assertEquals(ForecastType.SUNNY, mapper.getForecastType("Sunny"))
    }

    @Test fun assertItCanHandleTheUnknown() {
        assertEquals(ForecastType.UNKNOWN, mapper.getForecastType("dontEvenTry!"))
    }

    @Test fun assertCaseInsensitive() {
        assertEquals(ForecastType.MOSTLY_SUNNY, mapper.getForecastType("mOStly Sunny"))
    }
}
