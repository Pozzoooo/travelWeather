package pozzo.apps.travelweather.location.google

import com.google.gson.Gson
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import pozzo.apps.travelweather.core.FileLoader
import java.nio.file.Files
import java.nio.file.Paths

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
