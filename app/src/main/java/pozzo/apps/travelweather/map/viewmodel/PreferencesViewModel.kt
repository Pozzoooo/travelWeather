package pozzo.apps.travelweather.map.viewmodel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import pozzo.apps.travelweather.core.BaseViewModel
import pozzo.apps.travelweather.forecast.model.Day
import pozzo.apps.travelweather.map.business.PreferencesBusiness

class PreferencesViewModel(application: Application) : BaseViewModel(application) {
    val selectedDay = MutableLiveData<Day>()

    private val preferencesBusiness = PreferencesBusiness(application)

    init {
        readInitialSelectedDate()
    }

    private fun readInitialSelectedDate() {
        this.selectedDay.value = preferencesBusiness.getSelectedDay()
    }

    fun setSelectedDay(index: Int) {
        if (this.selectedDay.value?.index != index) {
            val selection = Day.getByIndex(index)
            preferencesBusiness.setSelectedDay(selection)
            this.selectedDay.value = selection
        }
    }
}
