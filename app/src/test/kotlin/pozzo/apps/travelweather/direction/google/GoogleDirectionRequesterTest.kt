package pozzo.apps.travelweather.direction.google

import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor

class GoogleDirectionRequesterTest {
    private lateinit var requester: GoogleDirectionRequester
    private lateinit var okHttp: OkHttpClient

    @Before fun setup() {
        okHttp = mock()
        requester = GoogleDirectionRequester(okHttp)
    }

    @Test fun assertRequest() {
        val fakeResponse = mock<Response>()
        val call = mock<Call> { on { execute() } doReturn fakeResponse }
        val request = ArgumentCaptor.forClass(Request::class.java)
        whenever(okHttp.newCall(request.capture())).thenReturn(call)

        val response = requester.request(LatLng(0.0, 0.0), LatLng(1.0, 1.0))
        val requestUrl = request.value.url().toString()

        assertNull(response)
        assertTrue(requestUrl.contains("origin=0.0,0.0"))
        assertTrue(requestUrl.contains("&destination=1.0,1.0"))
    }
}
