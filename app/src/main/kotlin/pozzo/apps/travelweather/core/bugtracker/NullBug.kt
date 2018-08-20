package pozzo.apps.travelweather.core.bugtracker

import android.app.Application

class NullBug : Bug() {
    override fun init(application: Application) { }
    override fun logException(exception: Exception) { }
    override fun logException(key: String, value: String, exception: Exception) { }
    override fun logEvent(event: String) { }
}
