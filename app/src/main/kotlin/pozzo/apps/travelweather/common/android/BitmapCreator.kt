package pozzo.apps.travelweather.common.android

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import pozzo.apps.travelweather.core.bugtracker.Bug
import java.lang.NullPointerException

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

    open fun fromResource(resourceId: Int) : BitmapDescriptor? {
        return try {
            BitmapDescriptorFactory.fromResource(resourceId)
        } catch (e: NullPointerException) {
            Bug.get().logException(e)//There seems to happen a null factory for some weird emulator
            null
        }
    }
}
