package pozzo.apps.travelweather.map.movement

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

class Movement {
    companion object {
        private const val MOVE_FACTOR = 0.07
    }

    val north = Direction()
    val south = Direction()
    val east = Direction()
    val west = Direction()

    fun buildCameraUpdate(initialBound: LatLngBounds): CameraUpdate {
        var southwestLatitude = initialBound.southwest.latitude
        var southwestLongitude = initialBound.southwest.longitude
        var northeastLatitude = initialBound.northeast.latitude
        var northeastLongitude = initialBound.northeast.longitude

        northeastLatitude += north.value * MOVE_FACTOR
        southwestLatitude += north.value * MOVE_FACTOR
        southwestLatitude -= south.value * MOVE_FACTOR
        northeastLatitude -= south.value * MOVE_FACTOR
        northeastLongitude += east.value * MOVE_FACTOR
        southwestLongitude += east.value * MOVE_FACTOR
        southwestLongitude -= west.value * MOVE_FACTOR
        northeastLongitude -= west.value * MOVE_FACTOR

        return CameraUpdateFactory.newLatLngBounds(LatLngBounds(
                LatLng(southwestLatitude, southwestLongitude),
                LatLng(northeastLatitude, northeastLongitude)
        ), 0)
    }

    fun hasMovement(): Boolean {
        return north.hasValue() || south.hasValue() || east.hasValue() || west.hasValue()
    }
}
