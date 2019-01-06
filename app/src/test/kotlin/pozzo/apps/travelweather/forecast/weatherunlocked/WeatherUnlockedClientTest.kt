package pozzo.apps.travelweather.forecast.weatherunlocked

import com.google.android.gms.maps.model.LatLng
import org.junit.Before
import org.junit.Test
import pozzo.apps.travelweather.core.TestInjector

class WeatherUnlockedClientTest {
    private lateinit var forecastClient: WeatherUnlockedClient

    @Before fun setup() {
        val appComponent = TestInjector.getAppComponent().build()
        val module = ForecastModuleWeatherUnlocked()
        forecastClient = module.forecastClient(appComponent.retrofitBuilder()) as WeatherUnlockedClient
    }

    @Test fun rockIt() {
        val weather = forecastClient.fromCoordinates(LatLng(-23.565939, -46.573784))
        println(weather)
    }
}
