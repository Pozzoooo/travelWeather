package pozzo.apps.travelweather.map.parser

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert
import org.junit.Test
import pozzo.apps.travelweather.forecast.model.*
import pozzo.apps.travelweather.forecast.model.point.WeatherPoint
import pozzo.apps.travelweather.map.model.Address
import java.util.concurrent.CancellationException

//TODO this test is being ignored by jacoco, I should add it back
@ExperimentalCoroutinesApi class WeatherPointsAdapterTest {
    private val scope = TestCoroutineScope()
    private val weatherPointsData = mock<MutableLiveData<Channel<WeatherPoint>>>()
    private val weatherPointsAdapter = WeatherPointsAdapter(weatherPointsData, scope)
    private val tomorrow = DayTime(Day.TOMORROW, Time.getDefault())
    private val weatherPoints = Channel<WeatherPoint>()
    private val route = Route(weatherPoints = weatherPoints)
    private val weatherPoint = WeatherPoint(
            Weather("", emptyList(), Address(LatLng(.0, .0), ""), PoweredBy(0)))

    @After fun tearDown() {
        scope.cleanupTestCoroutines()
    }

    @Test fun assertPostingChannel() = scope.runBlockingTest {
        weatherPointsAdapter.updateWeatherPoints(tomorrow, route)

        verify(weatherPointsData).postValue(any())
    }

    @Test fun assertMultipleRequests() = scope.runBlockingTest {
        val weatherPoints2 = Channel<WeatherPoint>()
        val route2 = Route(weatherPoints = weatherPoints2)

        weatherPointsAdapter.updateWeatherPoints(tomorrow, route)
        weatherPointsAdapter.updateWeatherPoints(tomorrow, route2)

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

    @Test fun assertMultipleRequestsWithChannelCapacity() = scope.runBlockingTest {
        val weatherPoints = Channel<WeatherPoint>(1)
        val weatherPoints2 = Channel<WeatherPoint>(1)
        val route = Route(weatherPoints = weatherPoints)
        val route2 = Route(weatherPoints = weatherPoints2)

        weatherPointsAdapter.updateWeatherPoints(tomorrow, route)
        weatherPointsAdapter.updateWeatherPoints(tomorrow, route2)

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

    @Test fun assertNotRefreshingIfNotCached() = scope.runBlockingTest {
        weatherPointsAdapter.refreshRoute(tomorrow, route)

        verify(weatherPointsData, never()).postValue(any())
    }

    @Test fun assertRefreshing() = scope.runBlockingTest {
        weatherPointsAdapter.updateWeatherPoints(tomorrow, route)
        weatherPointsAdapter.refreshRoute(tomorrow, route)

        weatherPoints.send(weatherPoint)
        weatherPoints.close()

        verify(weatherPointsData, times(2)).postValue(any())
    }
}
