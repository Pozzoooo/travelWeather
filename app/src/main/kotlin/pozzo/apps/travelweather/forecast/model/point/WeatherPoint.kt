package pozzo.apps.travelweather.forecast.model.point

import android.content.Context
import pozzo.apps.travelweather.forecast.ForecastTitleFormatter
import pozzo.apps.travelweather.forecast.model.Forecast
import pozzo.apps.travelweather.forecast.model.PoweredBy
import pozzo.apps.travelweather.forecast.model.Weather

class WeatherPoint(private val weather: Weather) :
        MapPoint(null, weather.latLng, weather.url, false, true) {

    val forecastSize = weather.forecasts.size
    val forecast: Forecast get() = weather.getForecast(day)
    override val icon get() = forecast.icon
    val poweredBy: PoweredBy get() = weather.poweredBy

    //TODO I need to improve performance, too many objects being created + I need string cachin
    override fun getTitle(context: Context): String {
        return ForecastTitleFormatter().createTitle(context, forecast)
    }
}
