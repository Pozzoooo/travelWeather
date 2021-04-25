package pozzo.apps.travelweather.map.movement

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

class Movement {
    companion object {
        private const val MOVE_FACTOR = 0.01
    }

    var north = false
    var south = false
    var east = false
    var west = false

    fun buildCameraUpdate(initialBound: LatLngBounds): CameraUpdate {
        var southwestLatitude = initialBound.southwest.latitude
        var southwestLongitude = initialBound.southwest.longitude
        var northeastLatitude = initialBound.northeast.latitude
        var northeastLongitude = initialBound.northeast.longitude

        if (north) {
            southwestLatitude += MOVE_FACTOR
            northeastLatitude += MOVE_FACTOR
        }
        if (south) {
            southwestLatitude -= MOVE_FACTOR
            northeastLatitude -= MOVE_FACTOR
        }
        if (east) {
            southwestLongitude += MOVE_FACTOR
            northeastLongitude += MOVE_FACTOR
        }
        if (west) {
            southwestLongitude -= MOVE_FACTOR
            northeastLongitude -= MOVE_FACTOR
        }

        return CameraUpdateFactory.newLatLngBounds(LatLngBounds(
                LatLng(southwestLatitude, southwestLongitude),
                LatLng(northeastLatitude, northeastLongitude)
        ), 0)
    }

    fun hasMovement(): Boolean {
        return north || south || east || west
    }
}
