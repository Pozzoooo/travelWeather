package pozzo.apps.travelweather.forecast.model.point

import android.content.Context
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import pozzo.apps.travelweather.forecast.ForecastTitleFormatter
import java.util.*

abstract class MapPoint(open val icon: BitmapDescriptor?,
                        open val position: LatLng,
                        open val redirectUrl: String?,
                        val isDraggable: Boolean = false,
                        val shouldFadeIn: Boolean = true,
                        var date: Calendar = GregorianCalendar.getInstance(),
                        var marker: Marker? = null) {

    abstract fun getTitle(context: Context, forecastTitleFormatter: ForecastTitleFormatter): String

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapPoint) return false

        if (position != other.position) return false

        return true
    }

    override fun hashCode(): Int {
        return position.hashCode()
    }
}
