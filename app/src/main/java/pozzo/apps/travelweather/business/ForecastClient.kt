package pozzo.apps.travelweather.business

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.model.Weather

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