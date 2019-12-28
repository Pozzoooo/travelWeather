package pozzo.apps.travelweather.core.userinputrequest

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.lifecycle.LifecycleOwner

class LocationPermissionRequest(private val callback: Callback) :
        PermissionRequest(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)) {
    interface Callback {
        fun granted(lifeCycleOwner: LifecycleOwner)
        fun denied()
    }

    override fun granted(lifeCycleOwner: LifecycleOwner) {
        callback.granted(lifeCycleOwner)
    }

    override fun denied() {
        callback.denied()
    }

    override fun code() = 0x1
}
