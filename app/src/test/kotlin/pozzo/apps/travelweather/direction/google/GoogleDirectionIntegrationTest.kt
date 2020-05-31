package pozzo.apps.travelweather.direction.google

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import pozzo.apps.travelweather.core.TestInjector
import pozzo.apps.travelweather.core.injection.AppComponent

@Ignore("For integration only")
class GoogleDirectionIntegrationTest {
    private lateinit var directionBusiness: GoogleDirection
    private lateinit var requester: GoogleDirectionRequester
    private lateinit var parser: GoogleResponseParser
    private lateinit var decoder: PolylineDecoder

    private lateinit var appComponent: AppComponent

    private val start = LatLng(53.380555, -6.159761)
    private val end = LatLng(53.376611, -6.169778)

    @Before fun setup() {
        appComponent = TestInjector.getAppComponent().build()

        requester = GoogleDirectionRequester(appComponent.okHttpClient())
        decoder = PolylineDecoder()
        parser = GoogleResponseParser(Gson())

        directionBusiness = GoogleDirection(requester, parser, decoder)
    }

    @Test fun assertReturnAsExpected() {
        val direction = directionBusiness.getDirection(start, end)

        Assert.assertEquals(78, direction!!.size)
    }
}
