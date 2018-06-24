package pozzo.apps.travelweather.firebase

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import pozzo.apps.travelweather.BuildConfig

class InstanceIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        /*
            val refreshedToken = FirebaseInstanceId.getInstance().token
            todo Think if there is any scenario which woudl make sense to save firebase token
         */
        if (BuildConfig.DEBUG) printTokenOnConsole()
    }

    private fun printTokenOnConsole() {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        println("refreshedToken: $refreshedToken")
    }
}
