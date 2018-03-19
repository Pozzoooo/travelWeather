package pozzo.apps.travelweather.map.viewmodel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.preference.PreferenceManager
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.core.BaseViewModel
import pozzo.apps.travelweather.forecast.model.Day

class PreferencesViewModel(application: Application) : BaseViewModel(application) {
    val selectedDay = MutableLiveData<Day>()

    init {
        readInitialSelectedDate()
    }

    private fun readInitialSelectedDate() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(getApplication())
        val selectedDay = preferences.getInt("selectedDay", R.id.rToday)
        this.selectedDay.value = Day.getByResourceId(selectedDay)
    }

    fun setSelectedDay(resourceId: Int) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(getApplication()).edit()
        preferences.putInt("selectedDay", resourceId).apply()
        preferences.apply()
        this.selectedDay.value = Day.getByResourceId(resourceId)
    }
}
