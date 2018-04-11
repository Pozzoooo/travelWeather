package pozzo.apps.travelweather.map.viewrequest

import android.Manifest
import android.arch.lifecycle.LifecycleOwner
import pozzo.apps.travelweather.core.Warning
import pozzo.apps.travelweather.map.viewmodel.MapViewModel

class LocationPermissionRequest(private val mapViewModel: MapViewModel) : PermissionRequest(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
    override fun granted(lifeCycleOwner: LifecycleOwner) {
        mapViewModel.setCurrentLocationAsStartPosition(lifeCycleOwner)
    }

    override fun denied() {
        mapViewModel.warn(Warning.PERMISSION_DENIED)
    }
}
