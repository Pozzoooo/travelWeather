package pozzo.apps.travelweather.core.bugtracker

import android.app.Application

//todo transform it into a library with provided dependencies
abstract class Bug {
    companion object {
        private var instance: Bug = NullBug()

        @JvmStatic
        fun get() = instance

        @JvmStatic
        fun setInstance(bug: Bug) {
            this.instance = bug
        }
    }

    fun logException(message: String) {
        logException(Exception(message))
    }

    abstract fun init(application: Application)
    abstract fun logException(exception: Exception)
    abstract fun logException(key: String, value: String, exception: Exception)
    abstract fun logEvent(event: String)
}
