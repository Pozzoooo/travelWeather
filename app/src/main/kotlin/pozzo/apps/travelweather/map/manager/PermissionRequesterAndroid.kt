package pozzo.apps.travelweather.map.manager

import android.app.Activity
import androidx.core.app.ActivityCompat
import pozzo.apps.travelweather.core.userinputrequest.PermissionRequest

class PermissionRequesterAndroid: PermissionRequester {
    override fun requestPermissions(activity: Activity, permissionRequest: PermissionRequest) {
        ActivityCompat.requestPermissions(activity, permissionRequest.permissions, permissionRequest.code())
    }
}
