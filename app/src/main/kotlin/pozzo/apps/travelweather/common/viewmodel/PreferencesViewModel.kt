package pozzo.apps.travelweather.common.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import pozzo.apps.travelweather.common.business.PreferencesBusiness
import pozzo.apps.travelweather.core.BaseViewModel
import pozzo.apps.travelweather.forecast.model.Day
import javax.inject.Inject

class PreferencesViewModel @ViewModelInject constructor(application: Application) : BaseViewModel(application) {
    val selectedDay = MutableLiveData<Day>()

    @Inject protected lateinit var preferencesBusiness: PreferencesBusiness

    init {
        readInitialSelectedDate()
    }

    private fun readInitialSelectedDate() {
        this.selectedDay.value = preferencesBusiness.getSelectedDay()
    }

    fun setSelectedDay(index: Int) {
        val hasChanged = this.selectedDay.value?.index != index
        if (hasChanged) {
            val selection = Day.getByIndex(index)
            preferencesBusiness.setSelectedDay(selection)
            this.selectedDay.value = selection
        }
    }
}
