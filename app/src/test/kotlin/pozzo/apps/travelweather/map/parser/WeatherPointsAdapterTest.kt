package pozzo.apps.travelweather.map.parser

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test
import pozzo.apps.travelweather.forecast.model.*
import pozzo.apps.travelweather.forecast.model.point.WeatherPoint
import pozzo.apps.travelweather.map.model.Address
import java.util.concurrent.CancellationException

//TODO this test is being ignore by jacoco, I should add it back
class WeatherPointsAdapterTest {
    private val weatherPointsData = mock<MutableLiveData<Channel<WeatherPoint>>>()
    private val weatherPointsAdapter = WeatherPointsAdapter(weatherPointsData)
    private val tomorrow = DayTime(Day.TOMORROW, Time.getDefault())
    private val weatherPoints = Channel<WeatherPoint>()
    private val route = Route(weatherPoints = weatherPoints)

    @Test fun assertPostingChannel() {
        weatherPointsAdapter.updateWeatherPoints(tomorrow, route)

        verify(weatherPointsData).postValue(any())
    }

    @Test fun assertMultipleRequests() {
        val weatherPoints2 = Channel<WeatherPoint>()
        val route2 = Route(weatherPoints = weatherPoints2)
        val weatherPoint = WeatherPoint(Weather("", emptyList(), Address(LatLng(.0, .0), ""), PoweredBy(0)))

        //TODO Ta com cara que vou ter que refatorar minhas coroutines
//        val scope = TestCoroutineScope()
        weatherPointsAdapter.updateWeatherPoints(tomorrow, route)
        weatherPointsAdapter.updateWeatherPoints(tomorrow, route2)

//        scope.runBlockingTest {
        runBlocking {
            try {
                weatherPoints.send(weatherPoint)
                weatherPoints.close()
                Assert.fail("Should have been canceled")
            } catch (e: CancellationException) {
                //Expected behaviour
            }
            weatherPoints2.send(weatherPoint)
            weatherPoints2.close()
        }
    }

    @Test fun assertMultipleRequestsWithChannelCapacity() {
        val weatherPoints = Channel<WeatherPoint>(1)
        val weatherPoints2 = Channel<WeatherPoint>(1)
        val route = Route(weatherPoints = weatherPoints)
        val route2 = Route(weatherPoints = weatherPoints2)
        val weatherPoint = WeatherPoint(Weather("", emptyList(), Address(LatLng(.0, .0), ""), PoweredBy(0)))

        weatherPointsAdapter.updateWeatherPoints(tomorrow, route)
        weatherPointsAdapter.updateWeatherPoints(tomorrow, route2)

        runBlocking {
            try {
                weatherPoints.send(weatherPoint)
                weatherPoints.close()
                Assert.fail("Should have been canceled")
            } catch (e: CancellationException) {
                //Expected behaviour
            }
            weatherPoints2.send(weatherPoint)
            weatherPoints2.close()
        }
    }

    @Test fun assertNotRefreshingIfNotCached() {
        weatherPointsAdapter.refreshRoute(tomorrow)

        verify(weatherPointsData, never()).postValue(any())
    }

    @Test fun assertRefreshing() {
        //TODO seems like I need a scope management to add this test back
//        weatherPointsAdapter.updateWeatherPoints(tomorrow, route)
//        weatherPointsAdapter.refreshRoute(tomorrow)
//
//        verify(weatherPointsData, times(2)).postValue(any())
    }
}
