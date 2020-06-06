package pozzo.apps.travelweather.direction

import com.google.android.gms.maps.model.LatLng

data class Direction(val steps: List<LatLng>, val duration: Duration?, val distance: Distance?) {

    fun isEmpty() = steps.isEmpty()
}

data class Distance(val value: Int, val text: String)
data class Duration(val value: Int, val text: String) {
    fun getMillis(): Long = value * 1000L
}
