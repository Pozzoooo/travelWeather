package pozzo.apps.travelweather.direction.google

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import pozzo.apps.travelweather.core.FileLoader

class GoogleDirectionTest {
    private lateinit var directionBusiness: GoogleDirection
    private lateinit var requester: GoogleDirectionRequester
    private lateinit var parser: GoogleResponseParser
    private lateinit var decoder: PolylineDecoder

    private val waypoints = listOf(LatLng(53.380555, -6.159761),
            LatLng(53.376611, -6.169778))

    @Before fun setup() {
        requester = mock()
        decoder = PolylineDecoder()
        parser = GoogleResponseParser(Gson())

        directionBusiness = GoogleDirection(requester, parser, decoder)
    }

    @Test fun assertReturnAsExpected() {
        val sample = FileLoader("googleDirectionResponseSample.json").string()
        whenever(requester.request(any())).thenReturn(sample)

        val direction = directionBusiness.getDirection(waypoints)

        assertEquals(78, direction!!.steps.size)
    }

    @Test fun shouldBeHandlingNullResponse() {
        val direction = directionBusiness.getDirection(waypoints)
        assertNull(direction)
    }

    @Test fun shouldHandleEmptyWaypoints() {
        val direction = directionBusiness.getDirection(emptyList())
        assertNull(direction)
    }
}
