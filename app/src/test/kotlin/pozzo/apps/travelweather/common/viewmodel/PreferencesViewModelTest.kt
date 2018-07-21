package pozzo.apps.travelweather.common.viewmodel

import android.app.Application
import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import pozzo.apps.travelweather.App
import pozzo.apps.travelweather.common.CommonModule
import pozzo.apps.travelweather.common.business.PreferencesBusiness
import pozzo.apps.travelweather.core.TestInjector
import pozzo.apps.travelweather.forecast.model.Day

class PreferencesViewModelTest {
    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock private lateinit var commonModule: CommonModule
    @Mock private lateinit var preferencesBusiness: PreferencesBusiness
    @Mock private lateinit var sharedPreferences: SharedPreferences

    private lateinit var preferencesViewModel: PreferencesViewModel

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)

        mockInjectors()

        preferencesViewModel = PreferencesViewModel(Mockito.mock(Application::class.java))
    }

    private fun mockInjectors() {
        val appComponent = TestInjector.getAppComponent()
        whenever(preferencesBusiness.getSelectedDay()).thenReturn(Day.TOMORROW)
        whenever(commonModule.preferencesBusiness(any(), any())).thenReturn(preferencesBusiness)
        whenever(commonModule.sharedPreferences(any())).thenReturn(sharedPreferences)
        appComponent.commonModule(commonModule)
        App.setComponent(appComponent.build())
    }

    @Test fun shouldHaveInitialized() {
        Assert.assertEquals(Day.TOMORROW, preferencesViewModel.selectedDay.value)
    }

    @Test fun shouldNotUpdateToTheSame() {
        preferencesViewModel.setSelectedDay(Day.TOMORROW.index)
        verify(preferencesBusiness, times(0)).setSelectedDay(any())
    }

    @Test fun shouldUpdateTheDay() {
        preferencesViewModel.setSelectedDay(Day.AFTER_TOMORROW.index)
        verify(preferencesBusiness).setSelectedDay(any())
    }
}
