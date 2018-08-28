package pozzo.apps.travelweather.common.business

import android.content.SharedPreferences
import pozzo.apps.travelweather.analytics.MapAnalytics
import pozzo.apps.travelweather.forecast.model.Day

class PreferencesBusiness(private val preferences: SharedPreferences, private val mapAnalytics: MapAnalytics) {
    companion object {
        private const val KEY_SELECTED_DAY = "selectedDay"
        private const val KEY_DAY_SELECTION_COUNT = "daySelectionCount"
    }

    fun getSelectedDay() : Day {
        val selectedDay = preferences.getInt(KEY_SELECTED_DAY, Day.TODAY.index)
        return Day.getByIndex(selectedDay)
    }

    fun setSelectedDay(day: Day) {
        val count = preferences.getInt(KEY_DAY_SELECTION_COUNT, 0)
        val edit = preferences.edit()
        edit.putInt(KEY_SELECTED_DAY, day.index)
        edit.putInt(KEY_DAY_SELECTION_COUNT, count + 1)
        edit.apply()
        mapAnalytics.sendDaySelectionChanged(day)
    }

    fun getDaySelectionCount() : Int = preferences.getInt(KEY_DAY_SELECTION_COUNT, 0)
}