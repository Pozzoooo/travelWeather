package pozzo.apps.travelweather.forecast.model

import java.util.*

class FormattedTemperature(private val temperature: Temperature) {

    fun getFormattedTemperature(): String {
        return if (isFahrenheitTemperature()) {
            getFormattedFahrenheit()
        } else {
            getFormattedCelsius()
        }
    }

    private fun getFormattedCelsius() = String.format("%.1f°C", temperature.getCelsius())

    private fun getFormattedFahrenheit() = String.format("%.1f°F", temperature.getFahrenheit())

    private fun isFahrenheitTemperature(): Boolean {
        return Locale.getDefault() == Locale.US
    }
}
