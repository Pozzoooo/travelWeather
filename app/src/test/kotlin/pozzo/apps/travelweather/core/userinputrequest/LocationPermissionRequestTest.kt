package pozzo.apps.travelweather.core.userinputrequest

import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class LocationPermissionRequestTest {
    @Mock private lateinit var callback: LocationPermissionRequest.Callback
    @Mock private lateinit var lifecycleOwner: LifecycleOwner

    private lateinit var locationPermissionRequest: LocationPermissionRequest

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)

        locationPermissionRequest = LocationPermissionRequest(callback)
    }

    @Test fun assertCallbackIsBeingCalled() {
        locationPermissionRequest.granted(lifecycleOwner)
        locationPermissionRequest.denied()

        verify(callback).granted(lifecycleOwner)
        verify(callback).denied()
    }

    @Test fun assertCodeIsNotChanging() {
        Assert.assertEquals(locationPermissionRequest.code(), locationPermissionRequest.code())
    }
}
