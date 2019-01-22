package pozzo.apps.travelweather.map.manager

import android.app.Activity
import pozzo.apps.travelweather.core.userinputrequest.PermissionRequest

interface PermissionRequester {
    fun requestPermissions(activity: Activity, permissionRequest: PermissionRequest)
}
