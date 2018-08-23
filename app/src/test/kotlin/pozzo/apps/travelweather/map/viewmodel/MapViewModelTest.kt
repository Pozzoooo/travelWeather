package pozzo.apps.travelweather.map.viewmodel

import android.app.Application
import android.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import pozzo.apps.travelweather.App
import pozzo.apps.travelweather.core.TestInjector
import pozzo.apps.travelweather.core.Warning

class MapViewModelTest {
    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mapViewModel: MapViewModel

    @Mock private lateinit var application: Application

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)
        mockInjectors()

        mapViewModel = MapViewModel(application)
    }

    private fun mockInjectors() {
        val appComponent = TestInjector.getAppComponent()
        App.setComponent(appComponent.build())
    }

    @Test fun assertWarn() {
        mapViewModel.warn(Warning.PERMISSION_DENIED)
        assertEquals(Warning.PERMISSION_DENIED, mapViewModel.warning.value)
    }
}
