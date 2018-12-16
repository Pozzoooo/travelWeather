package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.map.model.Address

data class Weather(
    val url: String,
    var address: Address? = null) {

    lateinit var forecasts: List<Forecast>

    val latLng: LatLng
        get() = LatLng(address!!.latitude, address!!.longitude)

    //TODO I need to handle an out of bounds in here
    fun getForecast(day: Day): Forecast = forecasts[day.index]
}
