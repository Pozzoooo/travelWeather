package pozzo.apps.travelweather.map.viewmodel

import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import pozzo.apps.travelweather.core.Warning
import pozzo.apps.travelweather.location.CurrentLocationRequester

class OnLocationPermissionChangeTest {
    private val currentLocationRequester: CurrentLocationRequester = mock()
    private val warning: MutableLiveData<Warning> = mock()
    private val updateSettings: () -> Unit = mock()

    private val onLocationPermissionChange = OnLocationPermissionChange(
            currentLocationRequester, warning, updateSettings)

    @Test fun assertBound() {
        onLocationPermissionChange.granted(mock())

        verify(updateSettings).invoke()
    }

    @Test fun assertWarningBound() {
        onLocationPermissionChange.denied()

        verify(warning).postValue(Warning.PERMISSION_DENIED)
    }
}
