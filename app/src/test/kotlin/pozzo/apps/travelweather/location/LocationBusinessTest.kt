package pozzo.apps.travelweather.location

import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.w3c.dom.Document
import pozzo.apps.travelweather.location.helper.GMapV2Direction

class LocationBusinessTest {
    private lateinit var locationBusiness: LocationBusiness

    @Mock private lateinit var directionParser: GMapV2Direction
    @Mock private lateinit var locationManager: LocationManager
    @Mock private lateinit var document: Document

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)

        locationBusiness = LocationBusiness(directionParser)
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

    @Test fun shouldReturnDirections() {
        val start = LatLng(0.0, 0.0)
        val finishPosition = LatLng(0.0, 0.0)
        val direction = listOf(start, finishPosition)

        whenever(directionParser.getDocument(start, finishPosition)).thenReturn(document)
        whenever(directionParser.getDirection(document)).thenReturn(direction)

        assertArrayEquals(direction.toTypedArray(),
                locationBusiness.getDirections(start, finishPosition)?.toTypedArray())
    }
}
