package pozzo.apps.travelweather.map.viewmodel

import android.app.Application
import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.LifecycleOwner
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.experimental.Unconfined
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import pozzo.apps.travelweather.App
import pozzo.apps.travelweather.common.android.BitmapCreator
import pozzo.apps.travelweather.common.android.BitmapCreatorTest
import pozzo.apps.travelweather.core.CoroutineSettings
import pozzo.apps.travelweather.core.TestInjector
import pozzo.apps.travelweather.core.Warning
import pozzo.apps.travelweather.core.userinputrequest.LocationPermissionRequest
import pozzo.apps.travelweather.core.userinputrequest.PermissionRequest
import pozzo.apps.travelweather.location.CurrentLocationRequester
import pozzo.apps.travelweather.location.LocationModuleFake
import pozzo.apps.travelweather.location.PermissionDeniedException

class MapViewModelTest {
    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mapViewModel: MapViewModel
    private lateinit var currentLocationRequester: CurrentLocationRequester

    @Mock private lateinit var application: Application
    @Mock private lateinit var lifecycleOwner: LifecycleOwner

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)
        mockInjectors()
        //todo tem alguma forma de simplificar toda essa parte de injecao e desse set instance?
        BitmapCreator.setInstance(BitmapCreatorTest())

        mapViewModel = MapViewModel(application)
    }

    private fun mockInjectors() {
        val appComponent = TestInjector.getAppComponent()
        val locationModule = LocationModuleFake()
        currentLocationRequester = locationModule.currentLocationRequester
        appComponent.locationModule(locationModule)
        App.setComponent(appComponent.build())
    }

    @Test fun assertWarn() {
        mapViewModel.warn(Warning.PERMISSION_DENIED)
        assertEquals(Warning.PERMISSION_DENIED, mapViewModel.warning.value)
    }

    @Test fun assetMapReadyFlow() {
        mapViewModel.onMapReady(lifecycleOwner)
        verify(currentLocationRequester).requestCurrentLocationRequestingPermission(lifecycleOwner)
    }

    @Test fun startAsCurrentLocation() {
        mapViewModel.setStartAsCurrentLocationRequestedByUser(lifecycleOwner)
        verify(currentLocationRequester).requestCurrentLocationRequestingPermission(lifecycleOwner)
    }

    @Test fun startAsCurrentLocationShouldHandlePermissionException() {
        whenever(currentLocationRequester.requestCurrentLocationRequestingPermission(lifecycleOwner))
                .thenThrow(PermissionDeniedException())
        mapViewModel.setStartAsCurrentLocationRequestedByUser(lifecycleOwner)
        assertTrue(mapViewModel.permissionRequest.value is LocationPermissionRequest)
    }

    @Test fun assertPermissionGrantedFlow() {
        val fakePermission = Mockito.mock(PermissionRequest::class.java)
        mapViewModel.onPermissionGranted(fakePermission, lifecycleOwner)
        verify(fakePermission).granted(lifecycleOwner)
        assertNull(mapViewModel.permissionRequest.value)
    }

    @Test fun assertPermissionDeniedFlow() {
        val fakePermission = Mockito.mock(PermissionRequest::class.java)
        mapViewModel.onPermissionDenied(fakePermission)
        verify(fakePermission).denied()
        assertNull(mapViewModel.permissionRequest.value)
    }

    @Test fun assertErrorIsDismissing() {
        mapViewModel.errorDismissed()
        assertNull(mapViewModel.error.value)
    }

    @Test fun assertClearStartPosition() {
        mapViewModel.clearStartPosition()
        assertNull(mapViewModel.routeData.value!!.startPoint)
    }

    @Test fun assertClearFinishPosition() {
        mapViewModel.clearFinishPosition()
        assertNull(mapViewModel.routeData.value!!.finishPoint)
    }

    @Test fun assertStartPosition() {
        val startPosition = LatLng(1.0, 1.0)
        mapViewModel.setStartPosition(startPosition)
        assertEquals(startPosition, mapViewModel.routeData.value!!.startPoint!!.position)
    }

    @Test fun assertFinishPosition() {
        val finishPosition = LatLng(2.0, 2.0)
        mapViewModel.setFinishPosition(finishPosition)
        assertEquals(finishPosition, mapViewModel.routeData.value!!.finishPoint!!.position)
    }
}
