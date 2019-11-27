package pozzo.apps.travelweather.map.parser

import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import pozzo.apps.travelweather.direction.DirectionWeatherFilter
import pozzo.apps.travelweather.forecast.ForecastBusiness
import pozzo.apps.travelweather.forecast.model.PoweredBy
import pozzo.apps.travelweather.forecast.model.Weather
import pozzo.apps.travelweather.forecast.model.point.MapPoint
import pozzo.apps.travelweather.map.model.Address

class MapPointCreatorTest {
    private lateinit var mapPointCreator : MapPointCreator

    @Mock private lateinit var forecastBusiness: ForecastBusiness
    @Mock private lateinit var weatherToMapPointParser: WeatherToMapPointParser

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)
        mapPointCreator = MapPointCreator(forecastBusiness, weatherToMapPointParser)
    }

    @Test fun assertChannelIsBeingFilled() {
        val points = listOf(LatLng(1.0, 2.0), LatLng(2.0, 3.0), LatLng(3.0, 4.0))
        whenever(forecastBusiness.forecast(any())).thenReturn(Weather("url", emptyList(), Address(LatLng(1.0, 2.0)), PoweredBy(0)))
        whenever(weatherToMapPointParser.parse(any())).thenReturn(Mockito.mock(MapPoint::class.java))

        val channel = mapPointCreator.createMapPointsAsync(points)
        var count = 0
        runBlocking {
            for (it in channel) {
                ++count
            }
        }
        Assert.assertEquals(points.size, count)
    }
}
