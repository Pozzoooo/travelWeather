package pozzo.apps.travelweather.forecast.weatherunlocked

import com.google.android.gms.maps.model.LatLng
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import pozzo.apps.travelweather.core.FileLoader
import pozzo.apps.travelweather.core.TestInjector
import pozzo.apps.travelweather.core.injection.AppComponent
import pozzo.apps.travelweather.forecast.ForecastClient
import pozzo.apps.travelweather.forecast.ForecastClientBase
import pozzo.apps.travelweather.forecast.ForecastType
import pozzo.apps.travelweather.forecast.darksky.DarkSkyClient
import pozzo.apps.travelweather.forecast.darksky.ForecastModuleDarkSky
import pozzo.apps.travelweather.forecast.model.Day

//todo add tests for over the limit requests
class WeatherUnlockedClientTest {
    companion object {
        private const val LATITUDE = -23.565939
        private const val LONGITUDE = -46.573784
        private val LAT_LNG = LatLng(LATITUDE, LONGITUDE)
    }

    private val forecastClients = ArrayList<ForecastClient>()

    private lateinit var mockWebServer: MockWebServer
    private lateinit var appComponent: AppComponent

    @Before fun setup() {
        appComponent = TestInjector.getAppComponent().build()
        setupMockServer()

        setupWeatherUnlocked()
        setupDarkSky()
    }

    private fun setupMockServer() {
        this.mockWebServer = MockWebServer()
    }

    private fun setupWeatherUnlocked() {
        val module = ForecastModuleWeatherUnlocked()
        val api = module.createApi(appComponent.retrofitBuilder(), enqueueRequest("weatherUnlockedSample.json"))

        forecastClients.add(WeatherUnlockedClient(api, "", "", module.forecastTypeMapper()))
    }

    private fun enqueueRequest(jsonFile: String) : String {
        mockWebServer.enqueue(MockResponse().setBody(FileLoader(jsonFile).string()))
        return mockWebServer.url("").toString()
    }

    private fun setupDarkSky() {
        val module = ForecastModuleDarkSky()
        val api = module.createApi(appComponent.retrofitBuilder(), enqueueRequest("darkSkySample.json"))

        forecastClients.add(DarkSkyClient(api, module.forecastTypeMapper()))
    }

    @After fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test fun assertParsingRequest() {
        forecastClients.forEach {
            val weather = it.fromCoordinates(LAT_LNG)!!

            Assert.assertTrue(weather.forecasts.isNotEmpty())
            Assert.assertEquals(LAT_LNG, weather.address.latLng)
            Assert.assertNotNull(weather.url)
            Assert.assertNotNull(weather.poweredBy)

            val forecast = weather.getForecast(Day.TODAY)
            Assert.assertEquals(ForecastType.SNOW, forecast.forecastType)
        }
    }
}
