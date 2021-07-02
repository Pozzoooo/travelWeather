package pozzo.apps.travelweather.direction.google

import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.*
import okhttp3.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import java.io.IOException
import kotlin.random.Random

class GoogleDirectionRequesterTest {
    private lateinit var requester: GoogleDirectionRequester
    private lateinit var okHttp: OkHttpClient

    companion object {
        private const val DEFAULT_RESPONSE = ""
    }

    @Before fun setup() {
        okHttp = mock()
        requester = GoogleDirectionRequester(okHttp)
    }

    private fun mockRequest(response: String = DEFAULT_RESPONSE): ArgumentCaptor<Request> {
        val body = mock<ResponseBody> {
            on { string() } doReturn response
        }
        val fakeResponse = mock<Response> {
            on { body() } doReturn body
        }
        val call = mock<Call> { on { execute() } doReturn fakeResponse }
        val request = ArgumentCaptor.forClass(Request::class.java)
        whenever(okHttp.newCall(request.capture())).thenReturn(call)
        return request
    }

    @Test fun assertRequest() {
        val request = mockRequest()

        val response = requester.request(listOf(LatLng(0.0, 0.0), LatLng(1.0, 1.0)))
        val requestUrl = request.value.url()

        assertEquals(DEFAULT_RESPONSE, response)
        assertEquals(requestUrl.queryParameter("origin"), "0.0,0.0")
        assertEquals(requestUrl.queryParameter("destination"), "1.0,1.0")
    }

    @Test fun shouldNotCrashOnExceptions() {
        val call = mock<Call> { on { execute() } doThrow RuntimeException("No Whatever! Fake!") }
        whenever(okHttp.newCall(any())).thenReturn(call)
        val response = requester.request(listOf(LatLng(0.0, 0.0), LatLng(1.0, 1.0)))
        assertNull(response)
    }

    @Test(expected = IOException::class) fun shouldReThrowOnNetworkIssue() {
        val call = mock<Call> { on { execute() } doThrow IOException("No Network! Fake!") }
        whenever(okHttp.newCall(any())).thenReturn(call)
        requester.request(listOf(LatLng(0.0, 0.0), LatLng(1.0, 1.0)))
    }

    @Test fun shouldContainWaypoints() {
        val request = mockRequest()

        val waypoints = listOf(
                LatLng(0.0, 0.0),
                LatLng(0.5, 0.5),
                LatLng(0.7, 0.7),
                LatLng(1.0, 1.0)
        )
        requester.request(waypoints)
        val requestUrl = request.value.url()

        assertEquals(requestUrl.queryParameter("waypoints"), "0.5,0.5|0.7,0.7")
    }

    @Test fun shouldNotExceedLimitUrlLength() {
        val request = mockRequest()

        val random = Random(10L)
        val waypoints = ArrayList<LatLng>()
        repeat(2048) {
            waypoints.add(LatLng(random.nextDouble(), random.nextDouble()))
        }

        requester.request(waypoints)
        val requestUrl = request.value.url().toString()

        assertTrue(requestUrl.length < 8193)
    }

    @Test fun shouldNotExceedWayPointCountLimit() {
        val request = mockRequest()

        val waypoints = listOf(
                LatLng(0.0, 0.0),
                LatLng(0.1, 0.0),
                LatLng(0.2, 0.0),
                LatLng(0.3, 0.0),
                LatLng(0.4, 0.0),
                LatLng(0.5, 0.0),
                LatLng(0.6, 0.0),
                LatLng(0.7, 0.0),
                LatLng(0.8, 0.0),
                LatLng(0.9, 0.0),
                LatLng(1.0, 0.0)
        )

        requester.request(waypoints)
        val requestUrl = request.value.url()

        assertEquals(requestUrl.queryParameter("waypoints"),
                "0.1,0.0|0.2,0.0|0.3,0.0|0.4,0.0|0.5,0.0|0.6,0.0|0.7,0.0|0.8,0.0")
    }

    @Test fun shouldHandleShortRange() {
        assertNull(requester.request(emptyList()))
        assertNull(requester.request(listOf(LatLng(.0, .0))))
    }
}
