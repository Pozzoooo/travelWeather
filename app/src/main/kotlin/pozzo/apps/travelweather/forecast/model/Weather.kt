package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.map.model.Address

data class Weather(
    val url: String,
    val forecasts: List<Forecast>,
    val address: Address) {

    val latLng: LatLng
        get() = address.latLng

    fun getForecast(day: Day): Forecast {
        val index = day.index
        return if (index < 0 || index >= forecasts.size) {
            /*
            TODO ok, so I decided to not handle the size, assuming that it will always return 10
                But I need to follow this bug report to make sure that it is a true assumption
                If I ever have to handle it properly, here are some tips:
                    - Route is the class that could receive the new weather size field
                    - DirectionBusiness:26 is where the route is created, I believe we could add the weather size to it, somehow
                    - WeatherPoint.forecast is the one I could size to check, might be a good idea to check multiple at a time
                    - Probably MapActivity.showMapPoints() could be a good moment to handle the size and fix the adapter
                    - Keep in mind that the user could already have selected a out of range day
             */
            Bug.get().logException(ArrayIndexOutOfBoundsException(
                    "Forecast out of range, tried: $index, but size was ${forecasts.size}"))
            forecasts.last()
        } else {
            forecasts.getOrNull(day.index) ?: forecasts.last()
        }
    }
}
