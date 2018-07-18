package pozzo.apps.travelweather.location.helper

import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class GeoCoderBusinessTest {
    private val validAddressString = "valid address"
    private val validAddress = Mockito.mock(Address::class.java)
    private lateinit var geoCoderBusiness: GeoCoderBusiness

    @Before fun setup() {
        val geocoder = Mockito.mock(Geocoder::class.java)
        whenever(geocoder.getFromLocationName(eq(validAddressString), any())).thenReturn(listOf(validAddress))

        geoCoderBusiness = GeoCoderBusiness(geocoder)
    }

    @Test fun shouldReturnNullWhenNullAddress() {
        assertNull(geoCoderBusiness.getPositionFromFirst(null))
        assertNull(geoCoderBusiness.getPositionFromFirst(""))
    }

    @Test fun shouldReturnValidAddressWhenValidStringAddress() {
        assertEquals(LatLng(0.0, 0.0), geoCoderBusiness.getPositionFromFirst(validAddressString))
    }
}
