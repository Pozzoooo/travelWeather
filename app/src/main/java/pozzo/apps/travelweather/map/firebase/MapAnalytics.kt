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

    fun sendDragFinishEvent() {
        sendFirebaseFab("finish")
    }

    fun sendDragDurationEvent(eventName: String, dragTime: Long) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, eventName)
        bundle.putLong(FirebaseAnalytics.Param.VALUE, dragTime)
        firebaseAnalytics.logEvent("dragDuration", bundle)
    }

    fun sendDrawerOpened() {
        firebaseAnalytics.logEvent("drawerOpened", null)
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
