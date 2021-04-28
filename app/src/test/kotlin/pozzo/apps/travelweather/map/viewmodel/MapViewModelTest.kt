package pozzo.apps.travelweather.map.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
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
import pozzo.apps.travelweather.forecast.model.PoweredBy
import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.forecast.model.Time
import pozzo.apps.travelweather.forecast.model.Weather
import pozzo.apps.travelweather.forecast.model.point.WeatherPoint
import pozzo.apps.travelweather.location.LocationModuleFake
import pozzo.apps.travelweather.location.PermissionDeniedException
import pozzo.apps.travelweather.map.model.Address
import java.io.IOException

class MapViewModelTest {
    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mapViewModel: MapViewModel
    private lateinit var locationModuleFake: LocationModuleFake
    private lateinit var directionModuleFake: DirectionModuleFake

    @Mock private lateinit var application: Application
    @Mock private lateinit var lifecycleOwner: LifecycleOwner

    private val start by lazy(LazyThreadSafetyMode.NONE) { LatLng(1.0, 2.0) }
    private val finish by lazy(LazyThreadSafetyMode.NONE) { LatLng(3.0, 4.0) }
    private val emptyRoute by lazy(LazyThreadSafetyMode.NONE) { Route() }

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

    @Test fun assertWeatherPointsAreBeingAdded() {
        runBlocking {
            val point = mockWeatherPoint()
            mockRoute(point)
            for (it in mapViewModel.weatherPointsData.value!!) {
                assertEquals(point, it)
            }
        }
    }

    private suspend fun mockRoute(weatherPoint: WeatherPoint) {
        val weatherPoints = Channel<WeatherPoint>(1)
        weatherPoints.send(weatherPoint)
        weatherPoints.close()
        val route = Route(weatherPoints = weatherPoints)
        whenever(directionModuleFake.directionBusiness.createRoute(any(), any())).thenReturn(route)

        createSampleRoute()
    }

    private fun mockWeatherPoint() =
            WeatherPoint(Weather("", emptyList(), Address(LatLng(.0, .0), ""), PoweredBy(0)))

    @Test fun assertWeatherPointsAreBeingRefreshed() {
        runBlocking {
            val point = mockWeatherPoint()
            mockRoute(point)
            mapViewModel.weatherPointsData.value = null

            mapViewModel.setSelectedDay(2)

            for (it in mapViewModel.weatherPointsData.value!!) {
                assertEquals(point, it)
            }
        }
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

    @Test fun assertNotCrashingOnConnectionError() {
        val address = "address"

        mapViewModel.error.value = Error.NO_CONNECTION
        whenever(locationModuleFake.geoCoderBusiness.getPositionFromFirst(address)).thenThrow(IOException())

        mapViewModel.searchAddress(address)
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
        mapViewModel.setSelectedDay(0)
        assertNull(mapViewModel.actionRequest.value)
    }

    @Test fun assertSelectedTimeUpdate() {
        val time = Time(10)
        mapViewModel.setSelectedTime(time)
        assertEquals(time, mapViewModel.selectedDayTime.value!!.time)
    }
}
