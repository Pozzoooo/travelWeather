package pozzo.apps.travelweather.map.viewmodel

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.core.Error
import pozzo.apps.travelweather.location.CurrentLocationRequester

class CurrentLocationBinder(private val currentLocationRequester: CurrentLocationRequester,
                            private val errorHandler: ErrorHandler,
                            private val onCurrentLocation: (LatLng) -> Unit) :
        CurrentLocationRequester.Callback {

    override fun onCurrentLocation(latLng: LatLng) {
        onCurrentLocation.invoke(latLng)
        currentLocationRequester.removeLocationObserver()
    }

    override fun onNotFound() {
        errorHandler.postError(Error.CANT_FIND_CURRENT_LOCATION)
    }
}
