package pozzo.apps.travelweather.forecast

import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import pozzo.apps.travelweather.core.FileLoader
import pozzo.apps.travelweather.core.TestInjector
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.core.injection.AppComponent
import pozzo.apps.travelweather.forecast.darksky.DarkSkyClient
import pozzo.apps.travelweather.forecast.darksky.ForecastModuleDarkSky
import pozzo.apps.travelweather.forecast.model.Day
import pozzo.apps.travelweather.forecast.openweather.ForecastModuleOpenWeather
import pozzo.apps.travelweather.forecast.openweather.OpenWeatherClient
import pozzo.apps.travelweather.forecast.weatherunlocked.ForecastModuleWeatherUnlocked
import pozzo.apps.travelweather.forecast.weatherunlocked.WeatherUnlockedClient
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class WeatherClientTest {
    companion object {
        private const val LATITUDE = -23.565939
        private const val LONGITUDE = -46.573784
        private val LAT_LNG = LatLng(LATITUDE, LONGITUDE)
    }

    private val forecastClients = ArrayList<ForecastClient>()

    private lateinit var mockWebServer: MockWebServer
    private lateinit var appComponent: AppComponent
    private lateinit var bug: Bug

    @Before fun setup() {
        appComponent = TestInjector.getAppComponent().build()
        bug = mock()
        Bug.setInstance(bug)
        setupMockServer()
    }

    private fun setupMockServer() {
        this.mockWebServer = MockWebServer()
    }

    @After fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test fun assertHappyPathRequest() {
        setupAllClients()

        forEachClient {
            val weather = it.fromCoordinates(LAT_LNG)!!

            Assert.assertTrue(weather.forecasts.isNotEmpty())
            Assert.assertEquals(LAT_LNG, weather.address.latLng)
            Assert.assertNotNull(weather.url)
            Assert.assertNotNull(weather.poweredBy)

            val forecast = weather.getForecast(GregorianCalendar.getInstance())
            Assert.assertEquals(ForecastType.SNOW, forecast.forecastType)
        }
    }

    private fun setupAllClients(shouldMockResponse: Boolean = true) {
        setupWeatherUnlocked(if (shouldMockResponse) "weatherUnlockedSample.json" else null)
        setupDarkSky(if (shouldMockResponse) "darkSkySample.json" else null)
        setupOpenWeatherClient(if (shouldMockResponse) "openWeatherSample.json" else null)
    }

    private fun setupWeatherUnlocked(mockedResponseJsonFile: String?) {
        val module = ForecastModuleWeatherUnlocked()
        val api = module.createApi(appComponent.retrofitBuilder(), enqueueRequest(mockedResponseJsonFile))

        forecastClients.add(WeatherUnlockedClient(api, "", "", module.forecastTypeMapper()))
    }

    private fun enqueueRequest(jsonFile: String?) : String {
        if (jsonFile != null) {
            mockWebServer.enqueue(MockResponse().setBody(FileLoader(jsonFile).string()))
        } else {
            mockWebServer.enqueue(MockResponse().setResponseCode(500))
        }
        return mockWebServer.url("").toString()
    }

    private fun setupDarkSky(mockedResponseJsonFile: String?) {
        val module = ForecastModuleDarkSky()
        val api = module.createApi(appComponent.retrofitBuilder(), enqueueRequest(mockedResponseJsonFile))

        forecastClients.add(DarkSkyClient(api, module.forecastTypeMapper()))
    }

    private fun setupOpenWeatherClient(mockedResponseJsonFile: String?) {
        val module = ForecastModuleOpenWeather()
        val api = module.createApi(appComponent.retrofitBuilder(), enqueueRequest(mockedResponseJsonFile))

        forecastClients.add(OpenWeatherClient(api, module.forecastTypeMapper(), ""))
    }

    private fun forEachClient(each: (ForecastClient) -> Unit) {
        forecastClients.forEach {
            println(" >>>> ${it::class.java.simpleName} <<<< ")
            each(it)
        }
    }

    @Test fun shouldNotCrashOnRequestError() {
        setupAllClients(false)

        forEachClient {
            val weather = it.fromCoordinates(LAT_LNG)

            Assert.assertNull(weather)
        }
        verify(bug, times(forecastClients.size)).logException(any<Exception>())
    }

    @Test fun shouldNotCrashAndReportOnErrorFormat() {
        val maxProvidersCount = 10
        repeat(maxProvidersCount) {
            enqueueRequest("googleDirectionResponseSample.json")
        }

        setupAllClients(false)
        assert(maxProvidersCount > forecastClients.size)

        forEachClient {
            val weather = it.fromCoordinates(LAT_LNG)

            Assert.assertNull(weather)
        }
        verify(bug, times(forecastClients.size)).logException(any<Exception>())
    }
}
