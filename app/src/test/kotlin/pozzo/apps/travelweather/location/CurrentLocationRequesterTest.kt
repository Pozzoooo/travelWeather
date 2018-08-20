package pozzo.apps.travelweather.location

import android.arch.lifecycle.LifecycleOwner
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import pozzo.apps.travelweather.core.PermissionChecker

class CurrentLocationRequesterTest {
    private lateinit var currentLocationRequester: CurrentLocationRequester

    @Mock private lateinit var permissionChecker: PermissionChecker
    @Mock private lateinit var locationBusiness: LocationBusiness
    @Mock private lateinit var locationManager: LocationManager
    @Mock private lateinit var locationLiveData: LocationLiveData
    @Mock private lateinit var lifecycleOwner: LifecycleOwner

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)

        currentLocationRequester = CurrentLocationRequester(
                permissionChecker, locationBusiness, locationManager, locationLiveData)
    }

    @Test fun shouldNotCrashWhenRemovingObserver() {
        currentLocationRequester.removeLocationObserver()
    }

    @Test(expected = PermissionDeniedException::class) fun shouldFailWhenPermissionIsDenied() {
        whenever(permissionChecker.hasPermission(any())).thenReturn(false)
        currentLocationRequester.requestCurrentLocationRequestingPermission(lifecycleOwner)
    }

    @Test fun shouldReturnCurrentKnownLocation() {
        val location = Mockito.mock(Location::class.java)
        whenever(location.latitude).thenReturn(1.0)
        whenever(location.longitude).thenReturn(2.0)
        whenever(locationBusiness.getCurrentKnownLocation(locationManager)).thenReturn(location)
        whenever(permissionChecker.hasPermission(any())).thenReturn(true)

        currentLocationRequester.callback = object : CurrentLocationRequester.Callback {
            override fun onCurrentLocation(latLng: LatLng) {
                assertEquals(1.0, latLng.latitude, 0.0)
                assertEquals(2.0, latLng.longitude, 0.0)
            }

            override fun onNotFound() {
                fail()
            }
        }
        currentLocationRequester.requestCurrentLocationRequestingPermission(lifecycleOwner)
    }

    @Test fun shouldNotCrashOnFullRequestFlow() {
        whenever(permissionChecker.hasPermission(any())).thenReturn(true)

        currentLocationRequester.requestCurrentLocationRequestingPermission(lifecycleOwner)
        currentLocationRequester.removeLocationObserver()
    }
}
