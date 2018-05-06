package pozzo.apps.travelweather.map.manager

import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import pozzo.apps.travelweather.map.ui.MapActivity
import pozzo.apps.travelweather.map.viewmodel.MapViewModel
import pozzo.apps.travelweather.map.viewrequest.LocationPermissionRequest
import pozzo.apps.travelweather.map.viewrequest.PermissionRequest

/**
 * Manages permission requests and results.
 */
class PermissionManager(private val mapActivity: MapActivity) {
    companion object {
        private const val REQ_PERMISSION_FOR_CURRENT_LOCATION = 0x1
    }

    private val viewModel: MapViewModel = ViewModelProviders.of(mapActivity).get(MapViewModel::class.java)

    fun requestPermissions(permissionRequest: PermissionRequest) {
        ActivityCompat.requestPermissions(mapActivity, permissionRequest.permissions, REQ_PERMISSION_FOR_CURRENT_LOCATION)
    }

    /**
     * @return true if handled
     */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) : Boolean {
        if (requestCode == REQ_PERMISSION_FOR_CURRENT_LOCATION) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[0])
                viewModel.onPermissionGranted(LocationPermissionRequest(viewModel), mapActivity)
            else
                viewModel.onPermissionDenied(LocationPermissionRequest(viewModel))
        } else {
            return false
        }
        return true
    }
}
