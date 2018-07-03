package pozzo.apps.travelweather.forecast

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert
import org.junit.Test
import pozzo.apps.travelweather.TestInjector

class ForecastBusinessTest {
    private val forecastBusiness: ForecastBusiness

    init {
        val appComponent = TestInjector.getAppComponent()
        forecastBusiness = ForecastBusiness(
                appComponent.forecastClient(), appComponent.forecastTypeMapper())
    }

    @Test fun requestWeather() {
        val weather = forecastBusiness.forecast(LatLng(40.781579, -74.358705))
        Assert.assertNotNull(weather!!.forecasts!![0].forecastType!!.iconId)
    }
}
