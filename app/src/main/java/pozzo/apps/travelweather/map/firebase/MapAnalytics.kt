package pozzo.apps.travelweather.map.firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import pozzo.apps.travelweather.core.Error
import pozzo.apps.travelweather.forecast.model.Day

class MapAnalytics(private val firebaseAnalytics: FirebaseAnalytics) {

    fun sendFirebaseUserRequestedCurrentLocationEvent() {
        sendFirebaseFab("currentLocation")
    }

    private fun sendFirebaseFab(itemName: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName)
        firebaseAnalytics.logEvent("fab", bundle)
    }

    fun sendClearRouteEvent() {
        sendFirebaseFab("clearRoute")
    }

    fun sendDragDurationEvent(eventName: String, dragTime: Long) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, eventName)
        bundle.putLong("milliseconds", dragTime)
        bundle.putString("dragLevel", dragLevel(dragTime))
        firebaseAnalytics.logEvent("dragDuration", bundle)
    }

    private fun dragLevel(dragTime: Long) : String {
        return when {
            dragTime < 300L -> "0.3"
            dragTime < 700L -> "0.7"
            dragTime < 2000L-> "2.0"
            else -> "2+"
        }
    }

    fun sendDaySelectionChanged(day: Day) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, day.name)
        firebaseAnalytics.logEvent("daySelection", bundle)
    }

    fun sendErrorMessage(it: Error) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, it.name)
        firebaseAnalytics.logEvent("errorMessage", bundle)
    }

    fun sendDisplayTopBarAction() {
        firebaseAnalytics.logEvent("displayTopBar", null)
    }

    fun sendSearchAddress() {
        firebaseAnalytics.logEvent("searchAddress", null)
    }
}
