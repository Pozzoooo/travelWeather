package pozzo.apps.travelweather.forecast

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import pozzo.apps.travelweather.TestInjector

class ForecastBusinessTest {
    private lateinit var forecastBusiness: ForecastBusiness

    @Before fun setup() {
        val appComponent = TestInjector.getAppComponent()
        forecastBusiness = ForecastBusiness(
                appComponent.forecastClient(), appComponent.forecastTypeMapper())
    }

    @Test fun requestWeather() {
        val weather = forecastBusiness.forecast(LatLng(40.781579, -74.358705))
        Assert.assertNotNull(weather!!.forecasts!![0].forecastType!!.iconId)
    }
}
