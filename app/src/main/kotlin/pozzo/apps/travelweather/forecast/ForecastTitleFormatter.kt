package pozzo.apps.travelweather.forecast

import android.content.Context
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.forecast.model.Forecast

/**
 * Pensei em fazer ele ser injetavel
 * e ai injetar somente o que for da lingua correta! Parece legal nao?
 */
class ForecastTitleFormatter {

    fun createTitle(context: Context, forecast: Forecast) : String {
        val forecastString = context.getString(forecast.forecastType.stringId)
        val min = context.getString(R.string.min)
        val max = context.getString(R.string.max)
        return "$forecastString - $min: ${forecast.low} $max: ${forecast.high}"
    }
}
