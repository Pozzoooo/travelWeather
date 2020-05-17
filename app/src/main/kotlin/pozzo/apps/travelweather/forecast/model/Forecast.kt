package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.BitmapDescriptor
import pozzo.apps.travelweather.forecast.ForecastType
import java.util.*

//TODO reorder once the feature is fully implemented
data class Forecast(var text: String,
                    var forecastType: ForecastType,
                    var high: Double, var low: Double, var date: Calendar = null!!) {

    val icon: BitmapDescriptor?
        get() {
            return forecastType.getIcon()
        }
}
