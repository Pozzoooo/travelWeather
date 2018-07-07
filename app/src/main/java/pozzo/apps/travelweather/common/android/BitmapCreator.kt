package pozzo.apps.travelweather.common.android

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class BitmapCreator {
    companion object {
        private var instance: BitmapCreator = BitmapCreator()

        @JvmStatic
        fun get() = instance

        @JvmStatic
        fun setInstance(bug: BitmapCreator) {
            this.instance = bug
        }
    }

    fun fromResource(resourceId: Int) : BitmapDescriptor = BitmapDescriptorFactory.fromResource(resourceId)
}
