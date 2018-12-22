package pozzo.apps.travelweather.core.bugtracker

import android.app.Application
import com.crashlytics.android.Crashlytics

class FabricBug : Bug() {

    override fun init(application: Application) {
    }

    override fun logException(exception: Exception) {
        Crashlytics.logException(exception)
    }

    override fun logException(key: String, value: String, exception: Exception) {
        Crashlytics.setString(key, value)
        logException(exception)
    }

    override fun logEvent(event: String) {
        Crashlytics.log(event)
    }
}
