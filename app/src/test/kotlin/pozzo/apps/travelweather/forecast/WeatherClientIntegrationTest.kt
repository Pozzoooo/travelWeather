package pozzo.apps.travelweather.forecast

import com.google.android.gms.maps.model.LatLng
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import pozzo.apps.travelweather.core.TestInjector
import pozzo.apps.travelweather.core.injection.AppComponent
import pozzo.apps.travelweather.forecast.darksky.DarkSkyClient
import pozzo.apps.travelweather.forecast.darksky.ForecastModuleDarkSky
import pozzo.apps.travelweather.forecast.openweather.ForecastModuleOpenWeather
import pozzo.apps.travelweather.forecast.openweather.OpenWeatherClient
import pozzo.apps.travelweather.forecast.weatherunlocked.ForecastModuleWeatherUnlocked
import pozzo.apps.travelweather.forecast.weatherunlocked.WeatherUnlockedClient
import java.util.*

@Ignore("For integration only")
class WeatherClientIntegrationTest {
    companion object {
        private const val LATITUDE = -23.565939
        private const val LONGITUDE = -46.573784
        private val LAT_LNG = LatLng(LATITUDE, LONGITUDE)
    }

    private val forecastClients = ArrayList<ForecastClient>()
    private lateinit var appComponent: AppComponent

    @Before fun setup() {
        appComponent = TestInjector.getAppComponent().build()
//        forecastClients.add(setupDarkSky())
//        forecastClients.add(setupWeatherUnlocked())
        forecastClients.add(setupOpenWeather())
    }

    private fun setupDarkSky(): DarkSkyClient {
        return ForecastModuleDarkSky().forecastClient(appComponent.retrofitBuilder()) as DarkSkyClient
    }

    private fun setupWeatherUnlocked(): WeatherUnlockedClient {
        return ForecastModuleWeatherUnlocked().forecastClient(appComponent.retrofitBuilder()) as WeatherUnlockedClient
    }

    private fun setupOpenWeather(): OpenWeatherClient {
        return ForecastModuleOpenWeather().forecastClient(appComponent.retrofitBuilder()) as OpenWeatherClient
    }

    @Test fun happyPath() {
        forecastClients.forEach {
            val weather = it.fromCoordinates(LAT_LNG)
            println(weather)
        }
    }
}
