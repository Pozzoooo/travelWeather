package pozzo.apps.travelweather

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat.checkSelfPermission

//TODO make it a standard class and inject it
object PermissionHelper {

    fun isGranted(permission: String, context: Context) : Boolean {
        return checkSelfPermission(context, permission) == PERMISSION_GRANTED
    }
}
