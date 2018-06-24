package pozzo.apps.travelweather.firebase

import com.google.firebase.iid.FirebaseInstanceIdService

class InstanceIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        /*
            val refreshedToken = FirebaseInstanceId.getInstance().token
            todo Think if there is any scenario which woudl make sense to save firebase token
         */
    }
}
