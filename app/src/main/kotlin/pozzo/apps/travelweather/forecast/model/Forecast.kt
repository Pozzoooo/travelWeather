package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.BitmapDescriptor
import pozzo.apps.travelweather.forecast.ForecastType

//TODO how to decide when to display celsius against fahrenheit?
data class Forecast(var text: String,
                    var forecastType: ForecastType,
                    var high: Double = .0, var low: Double = .0) {

    val icon: BitmapDescriptor?
        get() {
            return forecastType.getIcon()
        }
}
