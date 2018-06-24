package pozzo.apps.travelweather.map.business

import android.app.Application
import android.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import pozzo.apps.travelweather.forecast.model.Day
import pozzo.apps.travelweather.map.firebase.MapAnalytics

class PreferencesBusiness(private val application: Application) {
    companion object {
        private const val KEY_SELECTED_DAY = "selectedDay"
        private const val KEY_DAY_SELECTION_COUNT = "daySelectionCount"
    }
    private val mapAnalytics = MapAnalytics(FirebaseAnalytics.getInstance(application))
    private val preferences = PreferenceManager.getDefaultSharedPreferences(application)

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
