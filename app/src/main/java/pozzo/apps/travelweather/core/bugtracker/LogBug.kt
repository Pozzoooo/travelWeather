package pozzo.apps.travelweather.core.bugtracker

import android.app.Application

class LogBug : Bug() {
    override fun init(application: Application) { }

    override fun logException(exception: Exception) {
        exception.printStackTrace()
    }
    override fun logException(key: String, value: String, exception: Exception) {
        logException(exception)
        println("$key: $value")
    }

    override fun logEvent(event: String) {
        println(event)
    }
}
