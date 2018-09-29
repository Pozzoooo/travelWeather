package pozzo.apps.travelweather.location.google

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import pozzo.apps.travelweather.core.FileLoader

class GoogleDirectionTest {
    private lateinit var directionBusiness: GoogleDirection
    private lateinit var requester: GoogleDirectionRequester
    private lateinit var parser: GoogleResponseParser
    private lateinit var decoder: PolylineDecoder

    private val start = LatLng(53.380555, -6.159761)
    private val end = LatLng(53.376611, -6.169778)

    @Before fun setup() {
        requester = mock()
        decoder = PolylineDecoder()
        parser = GoogleResponseParser(Gson())

        directionBusiness = GoogleDirection(requester, parser, decoder)
    }

    @Test fun assertReturnAsExpected() {
        val sample = FileLoader("googleDirectionResponseSample.json").read().string()
        whenever(requester.request(start, end)).thenReturn(sample)

        val direction = directionBusiness.getDirection(start, end)

        Assert.assertEquals(78, direction!!.size)
    }

    @Test fun shouldBeHandlingNullResponse() {
        val direction = directionBusiness.getDirection(start, end)
        Assert.assertNull(direction)
    }
}
