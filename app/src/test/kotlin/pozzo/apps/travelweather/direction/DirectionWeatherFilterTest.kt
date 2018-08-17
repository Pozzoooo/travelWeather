package pozzo.apps.travelweather.direction

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DirectionWeatherFilterTest {
    private lateinit var directionWeatherFilter: DirectionWeatherFilter

    @Before fun setup() {
        directionWeatherFilter = DirectionWeatherFilter()
    }

    @Test fun checkMinDistanceForWeathers() {
        Assert.assertFalse(directionWeatherFilter.isMinDistanceToForecast(LatLng(.0, .0), LatLng(.0, .0)))
        Assert.assertTrue(directionWeatherFilter.isMinDistanceToForecast(LatLng(.0, .0), LatLng(50.0, 50.0)))
    }

    @Test fun assertRouteIsFilteredAsExpected() {
        val filteredList = directionWeatherFilter.getWeatherPointsLocations(generateList(5000))
        assertEquals(10, filteredList.size)
    }

    private fun generateList(size: Int) = MutableList(size) { LatLng(it * 0.0007, it * 0.0007) }

    @Test fun assertHandlingEmptyList() {
        val filtered = directionWeatherFilter.getWeatherPointsLocations(emptyList())
        assertTrue(filtered.isEmpty())
    }

    @Test fun assertSmallList() {
        val filteredList = directionWeatherFilter.getWeatherPointsLocations(generateList(900))
        assertEquals(1, filteredList.size)
    }

    @Test fun assertSuperSmallList() {
        val filteredList = directionWeatherFilter.getWeatherPointsLocations(generateList(50))
        assertEquals(1, filteredList.size)
    }
}
