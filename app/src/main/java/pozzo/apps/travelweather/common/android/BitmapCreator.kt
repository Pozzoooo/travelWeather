package pozzo.apps.travelweather.common.android

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

open class BitmapCreator {
    companion object {
        private var instance: BitmapCreator = BitmapCreator()

        @JvmStatic
        fun get() = instance

        @JvmStatic
        fun setInstance(bug: BitmapCreator) {
            this.instance = bug
        }
    }

    open fun fromResource(resourceId: Int) : BitmapDescriptor = BitmapDescriptorFactory.fromResource(resourceId)
}
