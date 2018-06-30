package pozzo.apps.travelweather.forecast

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.forecast.model.Weather

class ForecastBusiness {
    private val forecastClient = ForecastClientFactory.instance.getForecastClient()

    fun from(location: LatLng): Weather? {
        return forecastClient.fromCoordinates(location)
    }
}
