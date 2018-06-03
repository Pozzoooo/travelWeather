package pozzo.apps.travelweather.map.business

import android.app.Application
import android.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.forecast.model.Day
import pozzo.apps.travelweather.map.firebase.MapAnalytics

class PreferencesBusiness(private val application: Application) {
    companion object {
        private const val KEY_SELECTED_DAY = "selectedDay"
    }
    private val mapAnalytics = MapAnalytics(FirebaseAnalytics.getInstance(application))
    private val preferences = PreferenceManager.getDefaultSharedPreferences(application)

    fun getSelectedDay() : Day {
        val selectedDay = preferences.getInt(KEY_SELECTED_DAY, R.id.rToday)
        return Day.getByResourceId(selectedDay)
    }

    fun setSelectedDay(day: Day) {
        val edit = preferences.edit()
        edit.putInt(KEY_SELECTED_DAY, day.resourceId).apply()
        edit.apply()
        mapAnalytics.sendDaySelectionChanged(day)
    }
}
