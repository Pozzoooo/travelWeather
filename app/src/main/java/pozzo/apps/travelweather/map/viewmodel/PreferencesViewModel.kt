package pozzo.apps.travelweather.map.viewmodel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.preference.PreferenceManager
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.core.BaseViewModel

class PreferencesViewModel(application: Application) : BaseViewModel(application) {
    val selectedDay = MutableLiveData<Int>()

    init {
        readInitialSelectedDate()
    }

    private fun readInitialSelectedDate() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(getApplication())
        val selectedDay = preferences.getInt("selectedDay", R.id.rToday)
        this.selectedDay.value = selectedDay
    }

    fun setSelectedDay(selectedDay: Int) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(getApplication()).edit()
        preferences.putInt("selectedDay", selectedDay).apply()
        preferences.apply()

        this.selectedDay.value = selectedDay
    }
}
