package pozzo.apps.travelweather.core.bugtracker

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics

class FirebaseBug: Bug() {

    override fun init(application: Application) { }

    override fun logException(exception: Exception) {
        FirebaseCrashlytics.getInstance().recordException(exception)
    }

    override fun logException(key: String, value: String, exception: Exception) {
        FirebaseCrashlytics.getInstance().setCustomKey(key, value)
        logException(exception)
    }

    override fun logEvent(event: String) {
        FirebaseCrashlytics.getInstance().log(event)
    }
}
