package pozzo.apps.travelweather.forecast

import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import pozzo.apps.travelweather.core.TestInjector

class ForecastBusinessTest {
    companion object {
        private val LAT_LNG = LatLng(40.781579, -74.358705)
    }

    private lateinit var forecastBusiness: ForecastBusiness
    private lateinit var forecastModuleFake: ForecastModuleFake
    private lateinit var forecastClient: ForecastClient

    @Before fun setup() {
        forecastModuleFake = ForecastModuleFake()
        forecastClient = forecastModuleFake.forecastClient

        val appComponent = TestInjector.getAppComponent()
        appComponent.forecastModule(forecastModuleFake)

        forecastBusiness = ForecastBusiness(appComponent.build().forecastClients())
    }

    @Test fun requestWeather() {
        val weather = forecastBusiness.forecast(LAT_LNG)
        Assert.assertNotNull(weather!!.forecasts[0].forecastType!!.iconId)
    }

    @Test fun shouldHandleNullResult() {
        whenever(forecastClient.fromCoordinates(LAT_LNG)).thenReturn(null)
        val weather = forecastBusiness.forecast(LAT_LNG)
        Assert.assertNull(weather)
    }

    @Test fun shouldHandleErrorResult() {
        whenever(forecastClient.fromCoordinates(LAT_LNG)).thenThrow(RuntimeException("Ooops!"))
        val weather = forecastBusiness.forecast(LAT_LNG)
        Assert.assertNull(weather)
    }
}
