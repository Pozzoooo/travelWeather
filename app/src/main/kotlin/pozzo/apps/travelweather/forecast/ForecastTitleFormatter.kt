package pozzo.apps.travelweather.forecast

import android.content.Context
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.forecast.model.Forecast
import pozzo.apps.travelweather.forecast.model.FormattedTemperature
import pozzo.apps.travelweather.forecast.model.Temperature
import javax.inject.Inject

class ForecastTitleFormatter @Inject constructor() {

    fun createTitle(context: Context, forecast: Forecast): String {
        val forecastString = context.getString(forecast.forecastType.stringId)
        val min = context.getString(R.string.min)
        val max = context.getString(R.string.max)
        val low = parseTemperature(forecast.low)
        val high = parseTemperature(forecast.high)
        return "$forecastString - $min: $low $max: $high"
    }

    private fun parseTemperature(fahrenheitTemperature: Double): String {
        val temperature = Temperature()
        temperature.setFahrenheit(fahrenheitTemperature)
        return FormattedTemperature(temperature).getFormattedTemperature()
    }
}
