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
}
