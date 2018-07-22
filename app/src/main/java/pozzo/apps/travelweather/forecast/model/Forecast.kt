package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.BitmapDescriptor
import pozzo.apps.travelweather.forecast.ForecastType

data class Forecast(var date: String? = null,
                    var text: String? = null,
                    var forecastType: ForecastType? = null,
                    var high: Int = 0,
                    var low: Int = 0) {

    val icon: BitmapDescriptor?
        get() {
            return forecastType?.getIcon()
        }
}
