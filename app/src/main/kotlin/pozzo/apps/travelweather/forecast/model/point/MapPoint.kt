package pozzo.apps.travelweather.forecast.model.point

import android.content.Context
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import java.util.*

//todo is there a way to remove the nullability on all those fields?
abstract class MapPoint(open val icon: BitmapDescriptor?,
                        open val position: LatLng,
                        open val redirectUrl: String?,
                        val isDraggable: Boolean = false,
                        val shouldFadeIn: Boolean = true,
                        var date: Calendar = GregorianCalendar.getInstance(),
                        var marker: Marker? = null) {

    abstract fun getTitle(context: Context): String
}
