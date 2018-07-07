package pozzo.apps.travelweather.core.bugtracker

import android.app.Application

class LogBug : Bug() {
    override fun init(application: Application) { }

    override fun logException(exception: Exception) {
        exception.printStackTrace()
    }
}
