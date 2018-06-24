package pozzo.apps.travelweather.core.userinputrequest

import android.Manifest
import android.arch.lifecycle.LifecycleOwner

class LocationPermissionRequest(private val callback: Callback) : PermissionRequest(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
    companion object {
        interface Callback {
            fun granted(lifeCycleOwner: LifecycleOwner)
            fun denied()
        }
    }

    override fun granted(lifeCycleOwner: LifecycleOwner) {
        callback.granted(lifeCycleOwner)
    }

    override fun denied() {
        callback.denied()
    }

    override fun code() = 0x1
}
