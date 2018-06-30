package pozzo.apps.travelweather.forecast

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.forecast.model.Weather

interface ForecastClient {
    fun fromCoordinates(coordinates: LatLng): Weather?
}
