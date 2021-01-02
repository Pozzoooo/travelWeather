package pozzo.apps.travelweather.core

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import javax.inject.Inject

class PermissionChecker @Inject constructor(private val context: Context) {

    fun isGranted(permission: String): Boolean = Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1
            || ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}
