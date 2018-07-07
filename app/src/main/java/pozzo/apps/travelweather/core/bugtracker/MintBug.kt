package pozzo.apps.travelweather.core.bugtracker

import android.app.Application
import com.splunk.mint.Mint

class MintBug(private val key: String) : Bug() {

    override fun init(application: Application) {
        Mint.initAndStartSession(application, key)
    }

    override fun logException(exception: Exception) {
        Mint.logException(exception)
    }
}
