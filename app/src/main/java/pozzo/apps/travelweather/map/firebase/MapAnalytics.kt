package pozzo.apps.travelweather.map.firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import pozzo.apps.travelweather.forecast.model.Day

//todo enviar quando nao encontrar a rota
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
        bundle.putString(FirebaseAnalytics.Param.VALUE, dragTime.toString())
        firebaseAnalytics.logEvent("dragDuration", bundle)
    }

    fun sendDrawerOpened() {
        firebaseAnalytics.logEvent("drawerOpened", null)
    }

    fun sendDaySelectionChanged(day: Day) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.VALUE, day.name)
        firebaseAnalytics.logEvent("daySelection", bundle)
    }
}