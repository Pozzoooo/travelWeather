package pozzo.apps.travelweather.common.business

import android.content.SharedPreferences
import pozzo.apps.travelweather.analytics.MapAnalytics
import pozzo.apps.travelweather.forecast.model.Day

class PreferencesBusiness(private val preferences: SharedPreferences, private val mapAnalytics: MapAnalytics) {
    companion object {
        private const val KEY_SELECTED_DAY = "selectedDay"
        private const val KEY_DAY_SELECTION_COUNT = "daySelectionCount"
        private const val KEY_LAST_REMAINING_REQUEST_RESET = "lastRemainingRequestReset"
        private const val KEY_USED_REQUEST_COUNT = "usedRequestCount"
    }

    fun getSelectedDay() : Day {
        val selectedDay = preferences.getInt(KEY_SELECTED_DAY, Day.DEFAULT.index)
        return Day.getByIndex(selectedDay)
    }

    fun setSelectedDay(day: Day) {
        val editor = preferences.edit()

        updateDateSelection(day, editor)
        incrementDaySelectionCount(editor)

        editor.apply()

        notifyAnalyticsDaySelectionChange(day)
    }

    private fun updateDateSelection(day: Day, editor: SharedPreferences.Editor) {
        editor.putInt(KEY_SELECTED_DAY, day.index)
    }

    private fun incrementDaySelectionCount(editor: SharedPreferences.Editor) {
        val count = preferences.getInt(KEY_DAY_SELECTION_COUNT, 0)
        editor.putInt(KEY_DAY_SELECTION_COUNT, count + 1)
    }

    fun getDaySelectionCount() : Int = preferences.getInt(KEY_DAY_SELECTION_COUNT, 0)

    private fun notifyAnalyticsDaySelectionChange(day: Day) {
        mapAnalytics.sendDaySelectionChanged(day)
    }

    fun getLastRemainingRequestReset() : Long =
            preferences.getLong(KEY_LAST_REMAINING_REQUEST_RESET, 0L)

    fun updateLastRemainingRequestReset() {
        preferences.edit().putLong(KEY_LAST_REMAINING_REQUEST_RESET, System.currentTimeMillis()).apply()
    }

    fun getUsedRequestCount() : Int =
            preferences.getInt(KEY_USED_REQUEST_COUNT, 0)

    fun addUsedRequestCount(countToAdd: Int) {
        preferences.edit().putInt(KEY_USED_REQUEST_COUNT, getUsedRequestCount() + countToAdd).apply()
    }

    fun resetUsedRequestCount() {
        preferences.edit().putInt(KEY_USED_REQUEST_COUNT, 0).apply()
    }
}
