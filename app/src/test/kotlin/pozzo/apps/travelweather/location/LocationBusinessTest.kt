package pozzo.apps.travelweather.location

import android.location.Location
import android.location.LocationManager
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class LocationBusinessTest {
    private lateinit var locationBusiness: LocationBusiness

    @Mock private lateinit var locationManager: LocationManager

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)

        locationBusiness = LocationBusiness()
    }

    @Test fun shouldReturnNothingWhenNoServiceIsAvailable() {
        assertNull(locationBusiness.getCurrentKnownLocation(null))
    }

    @Test(expected = SecurityException::class) fun shouldThrowWhenNoProviderIsAvailable() {
        locationBusiness.getCurrentKnownLocation(locationManager)
    }

    @Test fun shouldReturnSomeLocation() {
        val location = Location("bla")
        val provider = "provider"

        whenever(locationManager.getBestProvider(any(), any())).thenReturn(provider)
        whenever(locationManager.getLastKnownLocation(provider)).thenReturn(location)

        assertEquals(location, locationBusiness.getCurrentKnownLocation(locationManager))
    }
}
