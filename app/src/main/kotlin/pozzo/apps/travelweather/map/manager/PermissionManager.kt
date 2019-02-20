package pozzo.apps.travelweather.map.manager

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.core.userinputrequest.PermissionRequest
import pozzo.apps.travelweather.map.viewmodel.MapViewModel

/**
 * Manages permission requests and results.
 */
class PermissionManager(private val activity: AppCompatActivity, private val viewModel: MapViewModel,
                        private val permissionRequester: PermissionRequester = PermissionRequesterAndroid()) {
    private val onGoingRequests = HashMap<Int, PermissionRequest>()

    fun requestPermissions(permissionRequest: PermissionRequest) {
        onGoingRequests[permissionRequest.code()] = permissionRequest
        permissionRequester.requestPermissions(activity, permissionRequest)
    }

    /**
     * @return true if handled
     */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) : Boolean {
        val permissionRequest = onGoingRequests[requestCode]
        if (permissionRequest == null) {
            Bug.get().logException("On going request gone missing $requestCode --- $permissions")
            return false
        }

        if (PackageManager.PERMISSION_GRANTED == grantResults.getOrNull(0))
            viewModel.onPermissionGranted(permissionRequest, activity)
        else
            viewModel.onPermissionDenied(permissionRequest)
        return true
    }
}
