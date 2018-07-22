package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.map.model.Address

data class Weather(
    val url: String,
    var address: Address? = null) {

    lateinit var forecasts: List<Forecast>

    val latLng: LatLng
        get() = LatLng(address!!.latitude, address!!.longitude)

    fun getForecast(day: Day): Forecast = forecasts[day.index]
}
