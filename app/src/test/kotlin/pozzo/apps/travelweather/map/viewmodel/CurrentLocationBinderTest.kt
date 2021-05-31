package pozzo.apps.travelweather.map.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import pozzo.apps.travelweather.core.Warning
import pozzo.apps.travelweather.location.CurrentLocationRequester

class CurrentLocationBinderTest {
    private val currentLocationRequester: CurrentLocationRequester = mock()
    private val warning: MutableLiveData<Warning> = mock()
    private val onCurrentLocation: (LatLng) -> Unit = mock()

    private val currentLocationBinder = CurrentLocationBinder(
            currentLocationRequester, warning, onCurrentLocation)

    @Test fun assertBound() {
        val latLng = LatLng(.0, .0)

        currentLocationBinder.onCurrentLocation(latLng)

        verify(onCurrentLocation).invoke(latLng)
    }

    @Test fun assertErrorBound() {
        currentLocationBinder.onNotFound()

        verify(warning).postValue(Warning.CANT_FIND_CURRENT_LOCATION)
    }
}
