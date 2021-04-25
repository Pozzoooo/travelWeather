package pozzo.apps.travelweather.map.movement

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import org.junit.Assert
import org.junit.Test

class EdgeDectectionTest {

    @Test fun moveOnBorderNortheast() {
        val edgeMovement = EdgeDectection()

        val result = edgeMovement.checkEdge(
                LatLngBounds(LatLng(.0, .0), LatLng(1.0, 1.0)),
                LatLng(.04, .04)
        )

        Assert.assertTrue(result.hasMovement())
    }

    @Test fun moveOnBorderSouthwest() {
        val edgeMovement = EdgeDectection()

        val result = edgeMovement.checkEdge(
                LatLngBounds(LatLng(.0, .0), LatLng(1.0, 1.0)),
                LatLng(.96, .96)
        )

        Assert.assertTrue(result.hasMovement())
    }

    @Test fun dontMoveOnCenter() {
        val edgeMovement = EdgeDectection()

        val result = edgeMovement.checkEdge(
                LatLngBounds(LatLng(.0, .0), LatLng(1.0, 1.0)),
                LatLng(.5, .5)
        )

        Assert.assertFalse(result.hasMovement())
    }
}
