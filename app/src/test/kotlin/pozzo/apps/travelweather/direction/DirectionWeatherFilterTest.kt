package pozzo.apps.travelweather.direction

import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DirectionWeatherFilterTest {
    private lateinit var directionWeatherFilter: DirectionWeatherFilter

    @Before fun setup() {
        directionWeatherFilter = DirectionWeatherFilter(mock())
    }

    @Test fun checkMinDistanceForWeathers() {
        assertFalse(directionWeatherFilter.isMinDistanceToForecast(LatLng(.0, .0), LatLng(.0, .0), .5))
        assertTrue(directionWeatherFilter.isMinDistanceToForecast(LatLng(.0, .0), LatLng(50.0, 50.0), .5))
    }

    @Test fun assertRouteIsFilteredAsExpected() {
        val filteredList = directionWeatherFilter.getWeatherPointsLocations(generateList(5000))
        assertEquals(9, filteredList.size)
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

    @Test fun assertLongList() {
        val filteredList = directionWeatherFilter.getWeatherPointsLocations(generateList(10000))
        assertEquals(19, filteredList.size)
    }

    @Test fun assertSuperLongList() {
        val filteredList = directionWeatherFilter.getWeatherPointsLocations(generateList(20000))
        assertEquals(17, filteredList.size)
    }

    @Test fun assertMaxList() {
        val filteredList = directionWeatherFilter.getWeatherPointsLocations(generateList(100000))
        assertEquals(20, filteredList.size)
    }
}
