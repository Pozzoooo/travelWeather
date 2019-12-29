package pozzo.apps.travelweather

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat.checkSelfPermission

class PermissionHelper {

    fun isGranted(permission: String, context: Context) : Boolean {
        return checkSelfPermission(context, permission) == PERMISSION_GRANTED
    }
}
