package pozzo.apps.travelweather.map

import android.Manifest
import pozzo.apps.travelweather.core.PermissionChecker

class MapSettings(private val permissionChecker: PermissionChecker) {

    fun isMyLocationEnabled(): Boolean =
            permissionChecker.isGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
                    || permissionChecker.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)
}
