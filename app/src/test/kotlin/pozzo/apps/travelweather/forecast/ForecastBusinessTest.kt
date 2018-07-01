package pozzo.apps.travelweather.forecast

import com.google.android.gms.maps.model.LatLng
import org.junit.Test
import pozzo.apps.travelweather.TestInjector

class ForecastBusinessTest {
    private val forecastBusiness: ForecastBusiness

    init {
        val appComponent = TestInjector.getAppComponent()
        forecastBusiness = appComponent.forecastBusiness()
    }

    @Test fun requestWeather() {
        val weather = forecastBusiness.forecast(LatLng(50.0, 50.0))
        print(weather?.url)
    }
}
