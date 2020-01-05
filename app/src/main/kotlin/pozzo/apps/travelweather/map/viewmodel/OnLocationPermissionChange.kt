package pozzo.apps.travelweather.map.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import pozzo.apps.travelweather.core.Warning
import pozzo.apps.travelweather.core.userinputrequest.LocationPermissionRequest
import pozzo.apps.travelweather.location.CurrentLocationRequester

class OnLocationPermissionChange(
        private val currentLocationRequester: CurrentLocationRequester,
        private val warning: MutableLiveData<Warning>,
        private val updateSettings: () -> Unit) :
        LocationPermissionRequest.Callback {

    override fun granted(lifeCycleOwner: LifecycleOwner) {
        currentLocationRequester.requestCurrentLocationRequestingPermission(lifeCycleOwner)
        updateSettings.invoke()
    }

    override fun denied() {
        warning.postValue(Warning.PERMISSION_DENIED)
    }
}
