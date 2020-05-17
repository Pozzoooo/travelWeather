package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.*
import org.junit.Test
import pozzo.apps.travelweather.forecast.ForecastType
import pozzo.apps.travelweather.map.model.Address
import java.util.*

class WeatherTest {
    private val latLng = LatLng(.1, .9)

    @Test fun assertLatLngBound() {
        assertEquals(latLng, createWeather().latLng)
    }

    private fun createWeather(forecasts: List<Forecast> = emptyList()) =
            Weather("url", forecasts, Address(latLng), PoweredBy(0))

    @Test fun assertHappyPath() {
        val forecast1 = createForecast(0)
        val forecast2 = createForecast(1)
        val forecast3 = createForecast(2)
        val forecast4 = createForecast(3)

        val weather = createWeather(listOf(forecast1, forecast2, forecast3, forecast4))
        val now = GregorianCalendar.getInstance()
        val laterToday = GregorianCalendar.getInstance()
        laterToday.roll(Calendar.MINUTE, 35)
        val twoDaysAfter = GregorianCalendar.getInstance()
        twoDaysAfter.roll(Calendar.DAY_OF_YEAR, 2)

        assertEquals(forecast1, weather.getForecast(now))
        assertEquals(forecast1, weather.getForecast(now))
        assertEquals(forecast3, weather.getForecast(twoDaysAfter))
    }

    private fun createForecast(daysPast: Int) =
            Forecast("", ForecastType.BREEZY, createDate(daysPast), .1, .2)

    private fun createDate(daysPast: Int): Calendar {
        return GregorianCalendar.getInstance().apply {
            roll(Calendar.DAY_OF_YEAR, daysPast)
        }
    }
}
