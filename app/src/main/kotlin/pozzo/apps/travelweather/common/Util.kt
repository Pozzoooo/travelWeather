package pozzo.apps.travelweather.common

import android.content.Context
import pozzo.apps.tools.AndroidUtil
import pozzo.apps.travelweather.R

interface Util {
    companion object {
        var instance: Util = NullUtil()

        operator fun invoke() = instance
    }

    fun openUrl(link: String, context: Context): Boolean
}

class NullUtil : Util {
    override fun openUrl(link: String, context: Context): Boolean {
        return true
    }
}

class Android : Util {
    override fun openUrl(link: String, context: Context): Boolean {
        return AndroidUtil.openUrl(context.getString(R.string.googlePlay), context)
    }
}
