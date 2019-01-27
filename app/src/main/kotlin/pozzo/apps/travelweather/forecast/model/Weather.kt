package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.map.model.Address

data class Weather(
    val url: String,
    val forecasts: List<Forecast>,
    val address: Address,
    val poweredBy: PoweredBy) {

    val latLng: LatLng
        get() = address.latLng

    fun getForecast(day: Day): Forecast {
        val index = day.index
        return if (index < 0 || index >= forecasts.size) {
            Bug.get().logException(ArrayIndexOutOfBoundsException(
                    "Forecast out of range, tried: $index, but size was ${forecasts.size}"))
            forecasts.last()
        } else {
            forecasts.getOrNull(day.index) ?: forecasts.last()
        }
    }
}
