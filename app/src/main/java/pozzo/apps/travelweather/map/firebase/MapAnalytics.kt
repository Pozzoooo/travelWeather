package pozzo.apps.travelweather.map.firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class MapAnalytics(private val firebaseAnalytics: FirebaseAnalytics) {

    fun sendFirebaseUserRequestedCurrentLocationEvent() {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "currentLocation")
        firebaseAnalytics.logEvent("fab", bundle)
    }
}