package pozzo.apps.travelweather.map.viewrequest

import android.arch.lifecycle.LifecycleOwner

/**
 * Dependency inversion would be welcome here
 */
abstract class PermissionRequest(val permissions: Array<String>) {
    abstract fun granted(lifeCycleOwner: LifecycleOwner)
    abstract fun denied()
}