package pozzo.apps.travelweather.forecast

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.forecast.model.Weather

class ForecastBusiness(private val forecastClient : ForecastClient,
                       private val forecastTypeMapper: ForecastTypeMapper) {

    fun from(location: LatLng): Weather? {
        val weather = forecastClient.fromCoordinates(location)
        weather?.let { enrich(weather) }
        return weather
    }

    private fun enrich(weather: Weather) {
        weather.forecasts?.forEach {
            it.forecastType = forecastTypeMapper.getForecastType(it)
        }
    }

    fun isMinDistanceToForecast(from: LatLng, to: LatLng): Boolean {
        val distance = Math.abs(from.latitude - to.latitude) + Math.abs(from.longitude - to.longitude)
        return distance > 0.5
    }
}
