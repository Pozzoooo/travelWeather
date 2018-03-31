package pozzo.apps.travelweather.map.viewrequest

import android.arch.lifecycle.LifecycleOwner

/**
 * Depdency inversion would be welcome here
 */
abstract class PermissionRequest(val permissions: Array<String>) {
    abstract fun execute(lifeCycleOwner: LifecycleOwner)
}
