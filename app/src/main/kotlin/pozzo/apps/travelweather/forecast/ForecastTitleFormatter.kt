package pozzo.apps.travelweather.forecast

import android.content.Context
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.forecast.model.Forecast
import java.util.*

/**
 * Pensei em fazer ele ser injetavel
 * e ai injetar somente o que for da lingua correta! Parece legal nao?
 */
class ForecastTitleFormatter {

    fun createTitle(context: Context, forecast: Forecast) : String {
        val forecastString = context.getString(forecast.forecastType.stringId)
        val min = context.getString(R.string.min)
        val max = context.getString(R.string.max)
        val low = parseTemperature(forecast.low)
        val high = parseTemperature(forecast.high)
        return "$forecastString - $min: $low $max: $high"
    }

    //TODO does it makes sense to create its own temperature object?
    private fun parseTemperature(fahrenheitTemperature: Double): String {
        return if (isFahrenheitTemperature()) {
            String.format("%.1f°F", fahrenheitTemperature)
        } else {
            String.format("%.1f°C", convertFahrenheitToCelsius(fahrenheitTemperature))
        }
    }

    private fun isFahrenheitTemperature(): Boolean {
        return Locale.getDefault() == Locale.US
    }

    private fun convertFahrenheitToCelsius(fahrenheitTemperature: Double): Double {
        return (fahrenheitTemperature - 32) * 5 / 9
    }
}
