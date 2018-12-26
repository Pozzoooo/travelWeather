package pozzo.apps.travelweather.map.parser

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import pozzo.apps.travelweather.forecast.model.Weather
import pozzo.apps.travelweather.map.model.Address

class WeatherToMapPointParserTest {
    private lateinit var parser: WeatherToMapPointParser

    @Before fun setup() {
        parser = WeatherToMapPointParser()
    }

    @Test fun assertParsing() {
        val weathers = listOf(
                Weather("", emptyList(), Address(LatLng(1.0, 2.0), "addr")),
                Weather("", emptyList(), Address(LatLng(1.0, 2.0), null))
        )

        weathers.forEach {
            val point = parser.parse(it)
            assertEquals(LatLng(1.0, 2.0), point!!.position)
        }
    }
}
