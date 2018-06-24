package pozzo.apps.travelweather.map.manager

import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import com.splunk.mint.Mint
import pozzo.apps.travelweather.map.ui.MapActivity
import pozzo.apps.travelweather.map.userinputrequest.PermissionRequest
import pozzo.apps.travelweather.map.viewmodel.MapViewModel

/**
 * Manages permission requests and results.
 */
class PermissionManager(private val mapActivity: MapActivity) {
    private val viewModel: MapViewModel = ViewModelProviders.of(mapActivity).get(MapViewModel::class.java)
    private val onGoingRequests = HashMap<Int, PermissionRequest>()

    fun requestPermissions(permissionRequest: PermissionRequest) {
        onGoingRequests[permissionRequest.code()] = permissionRequest
        ActivityCompat.requestPermissions(mapActivity, permissionRequest.permissions, permissionRequest.code())
    }

    /**
     * @return true if handled
     */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) : Boolean {
        val permissionRequest = onGoingRequests[requestCode]
        if (permissionRequest == null) {
            Mint.logException(Exception("On going request gone missing $requestCode --- $permissions"))
            return false
        }

        if (PackageManager.PERMISSION_GRANTED == grantResults[0])
            viewModel.onPermissionGranted(permissionRequest, mapActivity)
        else
            viewModel.onPermissionDenied(permissionRequest)
        return true
    }
}
