package pozzo.apps.travelweather.forecast

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.forecast.model.Weather
import java.lang.Exception

class ForecastBusiness(private val forecastClient : List<ForecastClient>) {

    fun forecast(location: LatLng): Weather? {
        forecastClient.forEach {
            try {
                val weather = it.fromCoordinates(location)
                if (weather != null) {
                    return weather
                }
            } catch (e: Exception) {
                Bug.get().logException(Exception("Falling back into the next forecastClient", e))
            }
        }
        Bug.get().logException(Exception("None of the forecast client got it working, returning null :("))
        return null
    }
}
