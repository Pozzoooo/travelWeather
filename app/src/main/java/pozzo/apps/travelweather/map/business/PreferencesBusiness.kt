package pozzo.apps.travelweather.map.business

import android.app.Application
import android.preference.PreferenceManager
import androidx.core.content.edit
import com.google.firebase.analytics.FirebaseAnalytics
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.forecast.model.Day
import pozzo.apps.travelweather.map.firebase.MapAnalytics

class PreferencesBusiness(private val application: Application) {
    companion object {
        private const val KEY_SELECTED_DAY = "selectedDay"
    }
    private val mapAnalytics = MapAnalytics(FirebaseAnalytics.getInstance(application))

    fun getSelectedDay() : Day {
        val preferences = PreferenceManager.getDefaultSharedPreferences(application)
        val selectedDay = preferences.getInt(KEY_SELECTED_DAY, R.id.rToday)
        return Day.getByResourceId(selectedDay)
    }

    fun setSelectedDay(day: Day) {
        PreferenceManager.getDefaultSharedPreferences(application).edit {
            putInt(KEY_SELECTED_DAY, day.resourceId).apply()
        }
        mapAnalytics.sendDaySelectionChanged(day)
    }
}
