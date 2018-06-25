package pozzo.apps.travelweather.direction

import android.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions

class DirectionLineBusiness {
    companion object {
        private const val LINE_WIDTH = 7F
        private const val LINE_COLOR = Color.BLUE
    }

    fun createDirectionLine(direction: List<LatLng>) : PolylineOptions =
            PolylineOptions().width(LINE_WIDTH).color(LINE_COLOR).addAll(direction)
}
