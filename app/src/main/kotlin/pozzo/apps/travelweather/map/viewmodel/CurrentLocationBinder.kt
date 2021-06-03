package pozzo.apps.travelweather.map.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.core.Warning
import pozzo.apps.travelweather.location.CurrentLocationRequester

class CurrentLocationBinder(private val currentLocationRequester: CurrentLocationRequester,
                            private val warning: MutableLiveData<Warning>,
                            private val onCurrentLocation: (LatLng) -> Unit) :
        CurrentLocationRequester.Callback {

    override fun onCurrentLocation(latLng: LatLng) {
        onCurrentLocation.invoke(latLng)
        currentLocationRequester.removeLocationObserver()
    }

    override fun onNotFound() {
        warning.postValue(Warning.CANT_FIND_CURRENT_LOCATION)
    }
}
