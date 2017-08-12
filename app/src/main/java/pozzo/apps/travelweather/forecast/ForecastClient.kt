package pozzo.apps.travelweather.forecast

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.forecast.model.Weather

/**
 * @since 12/08/17.
 */
interface ForecastClient {
    /**
     * Forecast from given location.
     */
    fun fromAddress(address: String): Weather?

    fun fromCoordinates(coordinates: LatLng): Weather?
}