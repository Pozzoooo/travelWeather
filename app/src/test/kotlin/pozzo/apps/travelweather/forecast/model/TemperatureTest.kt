package pozzo.apps.travelweather.forecast.model

import org.junit.Assert.*
import org.junit.Test

class TemperatureTest {

    @Test fun givenCelsiusShouldParsToFahrenheit() {
        val temperature = Temperature()

        temperature.setCelsius(10.1)

        assertEquals(50.1, temperature.getFahrenheit(), .1)
    }

    @Test fun givenFahrenheitToCelsiusShouldPars() {
        val temperature = Temperature()

        temperature.setFahrenheit(50.1)

        assertEquals(10.1, temperature.getCelsius(), .1)
    }
}
