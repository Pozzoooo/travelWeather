package pozzo.apps.travelweather.forecast

import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import pozzo.apps.travelweather.core.TestInjector
import retrofit2.Retrofit

class ForecastModuleAllTest {
    private lateinit var forecastModuleAll: ForecastModuleAll
    private lateinit var retrofitBuilder: Retrofit.Builder

    @Before fun setup() {
        retrofitBuilder = TestInjector.getAppComponent().build().retrofitBuilder()
        forecastModuleAll = ForecastModuleAll()
    }

    @Test fun assertRandomness() {
        val repeat = 100
        val maxMatch = repeat - 1
        var matchCount = 0
        var lastList = forecastModuleAll.forecastClients(retrofitBuilder, mock())
        repeat(repeat) {
            val list = forecastModuleAll.forecastClients(retrofitBuilder, mock())
            if (list[0].javaClass.name == lastList[0].javaClass.name) {
                ++matchCount
            }
            lastList = list
        }
        Assert.assertTrue("$matchCount < $maxMatch", matchCount <= maxMatch)
    }
}
