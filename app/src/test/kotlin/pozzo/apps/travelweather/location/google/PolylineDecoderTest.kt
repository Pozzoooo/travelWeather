package pozzo.apps.travelweather.location.google

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertArrayEquals
import org.junit.Before
import org.junit.Test

class PolylineDecoderTest {
    private lateinit var decoder: PolylineDecoder

    @Before fun setup() {
        decoder = PolylineDecoder()
    }

    @Test fun assertDecoderIsDecoding() {
        val poly = "uswdIz`sd@"
        val decoded = decoder.decode(poly)
        assertArrayEquals(arrayOf(LatLng(53.37419,-6.16478)), decoded.toTypedArray())
    }
}
