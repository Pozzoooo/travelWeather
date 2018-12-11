package pozzo.apps.travelweather.core.userinputrequest

import androidx.lifecycle.LifecycleOwner

/**
 * Dependency inversion would be welcome here
 */
abstract class PermissionRequest(val permissions: Array<String>) {
    abstract fun granted(lifeCycleOwner: LifecycleOwner)
    abstract fun denied()
    abstract fun code() : Int
}
