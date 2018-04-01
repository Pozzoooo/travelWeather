package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.map.model.Address

class Weather {
    var address: Address? = null
    var forecasts: Array<Forecast>? = null
    var url: String? = null

    val latLng: LatLng
        get() = LatLng(address!!.latitude, address!!.longitude)

    fun getForecast(day: Day): Forecast {
        return forecasts!![day.forecastIndex]
    }

    fun setForecasts(forecasts: List<Forecast>) {
        this.forecasts = forecasts.toTypedArray()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val weather = other as Weather

        return address?.equals(weather.address) == true
    }

    override fun hashCode(): Int {
        return if (address != null) address!!.hashCode() else 0
    }
}
