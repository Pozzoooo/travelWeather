package pozzo.apps.travelweather.map.parser

import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.channels.Channel
import org.junit.Test
import pozzo.apps.travelweather.forecast.model.Day
import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.forecast.model.point.WeatherPoint

//TODO write these tests
class WeatherPointsAdapterTest {
    private val weatherPointsData = mock<MutableLiveData<Channel<WeatherPoint>>>()
    private val weatherPointsAdapter = WeatherPointsAdapter(weatherPointsData)

    @Test fun assertPostingChannel() {
        val weatherPoints = Channel<WeatherPoint>()
        val route = Route(weatherPoints = weatherPoints)

        weatherPointsAdapter.updateWeatherPoints(Day.TOMORROW, route)

        verify(weatherPointsData).postValue(any())
    }
}
