package pozzo.apps.travelweather.map.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.*
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
import pozzo.apps.travelweather.core.Error
import pozzo.apps.travelweather.core.TestInjector
import pozzo.apps.travelweather.core.Warning
import pozzo.apps.travelweather.core.action.ActionRequest
import pozzo.apps.travelweather.core.action.ClearActionRequest
import pozzo.apps.travelweather.core.userinputrequest.LocationPermissionRequest
import pozzo.apps.travelweather.core.userinputrequest.PermissionRequest
import pozzo.apps.travelweather.direction.DirectionModuleFake
import pozzo.apps.travelweather.direction.DirectionNotFoundException
import pozzo.apps.travelweather.forecast.model.Day
import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.location.LocationModuleFake
import pozzo.apps.travelweather.location.PermissionDeniedException
import java.io.IOException

class MapViewModelTest {
    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mapViewModel: MapViewModel
    private lateinit var locationModuleFake: LocationModuleFake
    private lateinit var directionModuleFake: DirectionModuleFake

    @Mock private lateinit var application: Application
    @Mock private lateinit var lifecycleOwner: LifecycleOwner

    val start by lazy { LatLng(1.0, 2.0) }
    val finish by lazy { LatLng(3.0, 4.0) }
    val emptyRoute by lazy { Route() }

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)
        mockInjectors()
        //todo tem alguma forma de simplificar toda essa parte de injecao e desse set instance?
        BitmapCreator.setInstance(BitmapCreatorTest())

        mapViewModel = MapViewModel(application)
    }

    private fun mockInjectors() {
        val appComponent = TestInjector.getAppComponent()
        locationModuleFake = LocationModuleFake()
        directionModuleFake = DirectionModuleFake()
        appComponent.locationModule(locationModuleFake)
        appComponent.directionModule(directionModuleFake)
        App.setComponent(appComponent.build())
    }

    @Test fun assertWarn() {
        mapViewModel.warn(Warning.PERMISSION_DENIED)
        assertEquals(Warning.PERMISSION_DENIED, mapViewModel.warning.value)
    }

    @Test fun assetMapReadyFlow() {
        mapViewModel.onMapReady(lifecycleOwner)
        verify(locationModuleFake.currentLocationRequester).requestCurrentLocationRequestingPermission(lifecycleOwner)
    }

    @Test fun startAsCurrentLocation() {
        mapViewModel.setStartAsCurrentLocationRequestedByUser(lifecycleOwner)
        verify(locationModuleFake.currentLocationRequester).requestCurrentLocationRequestingPermission(lifecycleOwner)
    }

    @Test fun startAsCurrentLocationShouldHandlePermissionException() {
        whenever(locationModuleFake.currentLocationRequester.requestCurrentLocationRequestingPermission(lifecycleOwner))
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
        mapViewModel.setStartPosition(start)
        assertEquals(start, mapViewModel.routeData.value!!.startPoint!!.position)
    }

    @Test fun assertFinishPosition() {
        mapViewModel.setFinishPosition(finish)
        assertEquals(finish, mapViewModel.routeData.value!!.finishPoint!!.position)
    }

    @Test fun assertRouteWillBeUpdated() {
        whenever(directionModuleFake.directionBusiness.createRoute(any(), any())).thenReturn(emptyRoute)
        createSampleRoute()

        assertEquals(emptyRoute, mapViewModel.routeData.value)
    }

    private fun createSampleRoute() {
        mapViewModel.setStartPosition(start)
        mapViewModel.setFinishPosition(finish)

        assertFalse(mapViewModel.isShowingProgress.value!!)
    }

    @Test fun assertRouteNotFindErrorBeingHandled() {
        doAnswer { throw DirectionNotFoundException() }
                .whenever(directionModuleFake.directionBusiness).createRoute(any(), any())
        createSampleRoute()

        assertEquals(Error.CANT_FIND_ROUTE, mapViewModel.error.value)
    }

    @Test fun assertInternetErrorBeingHandled() {
        doAnswer { throw IOException() }
                .whenever(directionModuleFake.directionBusiness).createRoute(any(), any())
        createSampleRoute()

        assertEquals(Error.NO_CONNECTION, mapViewModel.error.value)
    }

    @Test fun backShouldFinishWhenAllIsEmpty() {
        mapViewModel.back()
        assertTrue(mapViewModel.shouldFinish.value!!)
    }

    @Test fun shouldDisplayTopBarWhenHidden() {
        mapViewModel.toggleSearch("")
        assertTrue(mapViewModel.isShowingSearch.value!!)
    }

    @Test fun shouldHideTopBarWhenDisplaing() {
        mapViewModel.toggleSearch("")
        mapViewModel.toggleSearch("")
        assertFalse(mapViewModel.isShowingSearch.value!!)
    }

    @Test fun assertDragActionAddsAPoint() {
        mapViewModel.flagDragActionFinished(start)
        assertEquals(start, mapViewModel.routeData.value!!.startPoint!!.position)
    }

    @Test fun assertSecondDragAddsFinishPosition() {
        mapViewModel.dragStarted()
        whenever(directionModuleFake.directionBusiness.createRoute(any(), any())).thenReturn(emptyRoute)
        mapViewModel.flagDragActionFinished(LatLng(0.0, 3.0))
        mapViewModel.flagDragActionFinished(LatLng(1.0, 2.0))
        assertEquals(emptyRoute, mapViewModel.routeData.value)
    }

    @Test fun assertSearchIsHappening() {
        val address = "address"

        whenever(locationModuleFake.geoCoderBusiness.getPositionFromFirst(address)).thenReturn(start)
        mapViewModel.searchAddress(address)
        assertEquals(start, mapViewModel.routeData.value!!.startPoint!!.position)
    }

    @Test fun assertClearAction() {
        mapViewModel.requestClear()
        assertTrue(mapViewModel.actionRequest.value is ClearActionRequest)
    }

    @Test fun assertActionAcceptsCleanRequest() {
        val action = Mockito.mock(ActionRequest::class.java)
        mapViewModel.actionRequestAccepted(action)
        assertNull(mapViewModel.actionRequest.value)

        mapViewModel.requestClear()
        mapViewModel.actionRequestAccepted(action)
        assertNull(mapViewModel.actionRequest.value)
        verify(action, times(2)).execute()
    }

    @Test fun assertActionDismissCleanRequest() {
        mapViewModel.actionRequestDismissed()
        assertNull(mapViewModel.actionRequest.value)

        mapViewModel.requestClear()
        mapViewModel.actionRequestDismissed()
        assertNull(mapViewModel.actionRequest.value)
    }

    @Test fun assertRateMeDialogIsDisplayed() {
        mapViewModel.selectedDayChanged(Day.TODAY)
        assertNull(mapViewModel.actionRequest.value)
    }
}
