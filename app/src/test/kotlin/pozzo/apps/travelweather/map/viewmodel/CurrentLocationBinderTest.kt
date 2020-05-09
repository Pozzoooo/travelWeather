package pozzo.apps.travelweather.map.viewmodel

import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import pozzo.apps.travelweather.core.Error
import pozzo.apps.travelweather.location.CurrentLocationRequester

class CurrentLocationBinderTest {
    private val currentLocationRequester: CurrentLocationRequester = mock()
    private val errorHandler: ErrorHandler = mock()
    private val onCurrentLocation: (LatLng) -> Unit = mock()

    private val currentLocationBinder = CurrentLocationBinder(
            currentLocationRequester, errorHandler, onCurrentLocation)

    @Test fun assertBound() {
        val latLng = LatLng(.0, .0)

        currentLocationBinder.onCurrentLocation(latLng)

        verify(onCurrentLocation).invoke(latLng)
    }

    @Test fun assertErrorBound() {
        currentLocationBinder.onNotFound()

        verify(errorHandler).postError(Error.CANT_FIND_CURRENT_LOCATION)
    }
}
