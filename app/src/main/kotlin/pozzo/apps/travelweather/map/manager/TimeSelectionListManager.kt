package pozzo.apps.travelweather.map.manager

import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Spinner
import pozzo.apps.travelweather.forecast.model.Time
import pozzo.apps.travelweather.map.factory.AdapterFactory

class TimeSelectionListManager(private val spinnerTimeSelection: Spinner,
                               private val adapterFactory: AdapterFactory,
                               callback: OnItemSelectedListener) {

    init {
        spinnerTimeSelection.onItemSelectedListener = callback
        setup()
    }

    private fun setup() {
        val adapter = adapterFactory.createArrayAdapter(spinnerTimeSelection.context, createTimeList())
        spinnerTimeSelection.adapter = adapter
    }

    private fun createTimeList(): Array<Time> {
        return Array(24) { Time(it) }
    }

    fun setSelection(time: Time) {
        spinnerTimeSelection.setSelection(time.hour)
    }
}
