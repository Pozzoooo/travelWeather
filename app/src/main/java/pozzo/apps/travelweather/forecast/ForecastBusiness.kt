package pozzo.apps.travelweather.forecast

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.forecast.model.Weather
import pozzo.apps.travelweather.forecast.yahoo.ForecastTypeMapperYahoo

//todo I need to isolate models from different layers
class ForecastBusiness {
    private val forecastClient = ForecastClientFactory.instance.getForecastClient()

    fun from(location: LatLng): Weather? {
        val weather = forecastClient.fromCoordinates(location)
        weather?.let { enrich(weather) }
        return weather
    }

    private fun enrich(weather: Weather) {
        weather.forecasts?.forEach {
            it.forecastType = ForecastTypeMapperYahoo.getForecastType(it)
        }
    }
}
