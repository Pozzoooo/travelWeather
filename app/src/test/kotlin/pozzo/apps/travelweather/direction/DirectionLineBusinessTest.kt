package pozzo.apps.travelweather.direction

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DirectionLineBusinessTest {
    private lateinit var directionLineBusiness: DirectionLineBusiness

    @Before fun setup() {
        directionLineBusiness = DirectionLineBusiness()
    }

    @Test fun assertLineIsBeingCreatedAsExpected() {
        val directionLine = listOf(
                LatLng(1.0, 1.0),
                LatLng(2.0, 1.0),
                LatLng(2.0, 2.0),
                LatLng(1.0, 2.0)
        )

        val polLine = directionLineBusiness.createDirectionLine(directionLine)
        assertArrayEquals(directionLine.toTypedArray(), polLine.points.toTypedArray())
    }

    @Test fun assertEmptyLine() {
        val polLine = directionLineBusiness.createDirectionLine(emptyList())
        assertTrue(polLine.points.isEmpty())
    }
}
