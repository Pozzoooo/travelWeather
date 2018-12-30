package pozzo.apps.travelweather.forecast

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.forecast.model.Weather

class ForecastBusiness(private val forecastClient : ForecastClient) {

    //todo uma idea era receber uma lista de clients, e cyclar por eles
    fun forecast(location: LatLng): Weather? {
        return forecastClient.fromCoordinates(location)
    }
}
