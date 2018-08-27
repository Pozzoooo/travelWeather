package pozzo.apps.travelweather.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.experimental.launch
import pozzo.apps.travelweather.core.CoroutineSettings.background
import pozzo.apps.travelweather.core.Error
import pozzo.apps.travelweather.forecast.model.Day

class MapAnalytics(private val firebaseAnalytics: FirebaseAnalytics) {

    fun sendFirebaseUserRequestedCurrentLocationEvent() = launch(background) {
        sendFirebaseFab("currentLocation")
    }

    private fun sendFirebaseFab(itemName: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName)
        firebaseAnalytics.logEvent("fab", bundle)
    }

    fun sendClearRouteEvent() = launch(background) {
        sendFirebaseFab("clearRoute")
    }

    fun sendDragDurationEvent(eventName: String, dragTime: Long) = launch(background) {
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

    fun sendDaySelectionChanged(day: Day) = launch(background) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, day.name)
        firebaseAnalytics.logEvent("daySelection", bundle)
    }

    fun sendErrorMessage(it: Error) = launch(background) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, it.name)
        firebaseAnalytics.logEvent("errorMessage", bundle)
    }

    fun sendDisplayTopBarAction() = launch(background) {
        firebaseAnalytics.logEvent("displayTopBar", null)
    }

    fun sendSearchAddress() = launch(background) {
        firebaseAnalytics.logEvent("searchAddress", null)
    }

    fun sendRateDialogShown() = launch(background) {
        firebaseAnalytics.logEvent("rateDialogShown", null)
    }

    fun sendIWantToRate() = launch(background) {
        firebaseAnalytics.logEvent("rateDialog", null)
    }
}
