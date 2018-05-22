package pozzo.apps.travelweather.forecast.model.point

import pozzo.apps.travelweather.forecast.model.Day
import pozzo.apps.travelweather.forecast.model.Forecast
import pozzo.apps.travelweather.forecast.model.Weather

class WeatherPoint(private val weather: Weather) :
        MapPoint(null, null, weather.latLng, weather.url, false, true) {

    var day: Day = Day.TODAY
    val forecast : Forecast get() = weather.getForecast(day)
    override val icon get() = forecast.icon
    override val title get() = forecast.text
}
