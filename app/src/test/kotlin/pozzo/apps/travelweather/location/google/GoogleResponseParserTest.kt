package pozzo.apps.travelweather.location.google

import com.google.gson.Gson
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths

class GoogleResponseParserTest {
    private lateinit var parser: GoogleResponseParser

    @Before fun setup() {
        parser = GoogleResponseParser(Gson())
    }

    @Test fun assertParsing() {
        val fileUrl = javaClass.classLoader!!.getResource("googleDirectionResponseSample.json")!!
        val sample = String(Files.readAllBytes(Paths.get(fileUrl.toURI())))
        val parsed = parser.parse(sample)

        Assert.assertEquals("uswdIz`sd@a@hAWt@s@jBM\\IRMZ_A~BYn@q@`BQd@Sb@e@jAQb@Qb@O^Sd@Sh@[|@CH",
                parsed!!.routes[0].legs[0].steps[0].polyline.points)
    }
}
