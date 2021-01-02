package pozzo.apps.travelweather.common.business

import android.content.SharedPreferences
import pozzo.apps.travelweather.analytics.MapAnalytics
import pozzo.apps.travelweather.forecast.model.Day
import javax.inject.Inject

class PreferencesBusiness @Inject constructor(private val preferences: SharedPreferences,
                                              private val mapAnalytics: MapAnalytics) {
    companion object {
        private const val KEY_SELECTED_DAY = "selectedDay"
        private const val KEY_DAY_SELECTION_COUNT = "daySelectionCount"
        private const val KEY_LAST_REMAINING_REQUEST_RESET_TIME = "lastRemainingRequestReset"
        private const val KEY_USED_REQUEST_COUNT = "usedRequestCount"
    }

    fun getSelectedDay(): Day {
        val selectedDay = preferences.getInt(KEY_SELECTED_DAY, Day.DEFAULT.index)
        return Day.getByIndex(selectedDay)
    }

    fun setSelectedDay(day: Day) {
        val count = preferences.getInt(KEY_DAY_SELECTION_COUNT, 0)

        preferences.edit()
                .putInt(KEY_SELECTED_DAY, day.index)
                .putInt(KEY_DAY_SELECTION_COUNT, count + 1)
                .apply()

        mapAnalytics.sendDaySelectionChanged(day)
    }

    fun getDaySelectionCount(): Int = preferences.getInt(KEY_DAY_SELECTION_COUNT, 0)

    fun getLastRemainingRequestReset(): Long =
            preferences.getLong(KEY_LAST_REMAINING_REQUEST_RESET_TIME, 0L)

    fun getUsedRequestCount(): Int =
            preferences.getInt(KEY_USED_REQUEST_COUNT, 0)

    fun addUsedRequestCount(countToAdd: Int) {
        preferences.edit().putInt(KEY_USED_REQUEST_COUNT, getUsedRequestCount() + countToAdd).apply()
    }

    fun resetUsedRequestCount() {
        preferences.edit()
                .putLong(KEY_LAST_REMAINING_REQUEST_RESET_TIME, System.currentTimeMillis())
                .putInt(KEY_USED_REQUEST_COUNT, 0)
                .apply()
    }
}
