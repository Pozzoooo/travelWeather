package pozzo.apps.travelweather.common.android

import com.google.android.gms.maps.model.BitmapDescriptor
import org.mockito.Mockito

class BitmapCreatorTest : BitmapCreator() {

    override fun fromResource(resourceId: Int): BitmapDescriptor {
        return Mockito.mock(BitmapDescriptor::class.java)
    }
}
