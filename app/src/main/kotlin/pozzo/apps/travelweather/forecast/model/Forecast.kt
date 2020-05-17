package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.BitmapDescriptor
import pozzo.apps.travelweather.forecast.ForecastType
import java.util.*

data class Forecast(var text: String,
                    var forecastType: ForecastType,
                    var dateTime: Calendar,
                    var high: Double, var low: Double) {

    val icon: BitmapDescriptor?
        get() {
            return forecastType.getIcon()
        }
}
