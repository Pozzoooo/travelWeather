package pozzo.apps.travelweather.direction.google

import com.google.gson.Gson
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import pozzo.apps.travelweather.core.FileLoader

class GoogleResponseParserTest {
    private lateinit var parser: GoogleResponseParser

    @Before fun setup() {
        parser = GoogleResponseParser(Gson())
    }

    @Test fun assertParsing() {
        val sample = FileLoader("googleDirectionResponseSample.json").read().string()
        val parsed = parser.parse(sample)
        Assert.assertNotNull(parsed!!.routes[0].legs[0].steps[0].polyline.points)
    }
}
