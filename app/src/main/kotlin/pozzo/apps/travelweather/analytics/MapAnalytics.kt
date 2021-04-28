package pozzo.apps.travelweather.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pozzo.apps.travelweather.core.CoroutineSettings.background
import pozzo.apps.travelweather.core.Error
import pozzo.apps.travelweather.forecast.model.Day
import pozzo.apps.travelweather.forecast.model.Time

class MapAnalytics(private val firebaseAnalytics: FirebaseAnalytics) {

    fun sendFirebaseUserRequestedCurrentLocationEvent() = GlobalScope.launch(background) {
        sendFirebaseFab("currentLocation")
    }

    private fun sendFirebaseFab(itemName: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName)
        firebaseAnalytics.logEvent("fab", bundle)
    }

    fun sendClearRouteEvent() = GlobalScope.launch(background) {
        sendFirebaseFab("clearRoute")
    }

    fun sendDragDurationEvent(eventName: String, dragTime: Long) = GlobalScope.launch(background) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, eventName)
        bundle.putLong("tenthOfSecond", dragTime / 100L)
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

    fun sendDaySelectionChanged(day: Day) = GlobalScope.launch(background) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, day.name)
        firebaseAnalytics.logEvent("daySelection", bundle)
    }

    fun sendTimeSelectionChanged(time: Time) = GlobalScope.launch(background) {
        val bundle = Bundle()
        bundle.putInt(FirebaseAnalytics.Param.ITEM_NAME, time.hour)
        firebaseAnalytics.logEvent("timeSelection", bundle)
    }

    fun sendErrorMessage(it: Error) = GlobalScope.launch(background) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, it.name)
        firebaseAnalytics.logEvent("errorMessage", bundle)
    }

    fun sendShowSearch() = GlobalScope.launch(background) {
        firebaseAnalytics.logEvent("showSearch", null)
    }

    fun sendHideSearch() = GlobalScope.launch(background) {
        firebaseAnalytics.logEvent("hideSearch", null)
    }

    fun sendSearchAddress() = GlobalScope.launch(background) {
        firebaseAnalytics.logEvent("searchAddress", null)
    }

    fun sendRateDialogShown() = GlobalScope.launch(background) {
        firebaseAnalytics.logEvent("rateDialogShown", null)
    }

    fun sendIWantToRate() = GlobalScope.launch(background) {
        firebaseAnalytics.logEvent("pressedYesToRate", null)
    }

    fun sendEmptyForecastCountByRoute() = GlobalScope.launch(background) {
        sendForecastCountByRoute("empty", 0, 0)
    }

    fun sendSingleForecastCountByRoute(directionLineSize: Int) = GlobalScope.launch(background) {
        sendForecastCountByRoute("single", 0, directionLineSize)
    }

    fun sendForecastCountByRoute(weatherCount: Int, directionLineSize: Int) = GlobalScope.launch(background) {
        sendForecastCountByRoute("multiple", weatherCount, directionLineSize)
    }

    private fun sendForecastCountByRoute(eventName: String, weatherCount: Int, directionLineSize: Int) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, eventName)
        bundle.putInt("weatherCount", weatherCount)
        bundle.putInt("directionLineSize", directionLineSize)
        firebaseAnalytics.logEvent("forecastRequest", bundle)
    }

    fun sendKnownException(name: String, description: String) = GlobalScope.launch(background) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        bundle.putString("description", description)
        firebaseAnalytics.logEvent("exception", bundle)
    }
}
