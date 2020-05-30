package pozzo.apps.travelweather.forecast.model.point

import android.content.Context
import pozzo.apps.travelweather.forecast.ForecastTitleFormatter
import pozzo.apps.travelweather.forecast.model.Forecast
import pozzo.apps.travelweather.forecast.model.PoweredBy
import pozzo.apps.travelweather.forecast.model.Weather
import java.util.*

class WeatherPoint(private val weather: Weather) :
        MapPoint(null, weather.latLng, weather.url, false, true) {

    val forecastSize = weather.forecasts.size
    val forecast: Forecast get() = weather.getForecast(date)
    override val icon get() = forecast.icon
    val poweredBy: PoweredBy get() = weather.poweredBy

    override fun getTitle(context: Context, forecastTitleFormatter: ForecastTitleFormatter): String {
        return forecastTitleFormatter.createTitle(context, forecast)
    }
}
