package pozzo.apps.travelweather.map.manager

import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import pozzo.apps.travelweather.forecast.model.Day
import pozzo.apps.travelweather.map.factory.AdapterFactory

class DaySelectionListManager(private val spinnerDaySelection: Spinner,
                              private val adapterFactory: AdapterFactory,
                              callback: OnItemSelectedListener) {
    companion object {
        private const val DEFAULT_SIZE = 7
    }

    private var size = 0

    init {
        updateDaySelections(DEFAULT_SIZE)
        spinnerDaySelection.onItemSelectedListener = callback
    }

    fun updateDaySelections(newSize: Int) {
        if (size != newSize && size != calculateNewSize(newSize)) {
            size = calculateNewSize(newSize)
            val adapter = adapterFactory.createArrayAdapter(
                    spinnerDaySelection.context, createNewListBasedOnSize(size))
            updateAdapterAndSelection(adapter, calculateNewSelection(size))
        }
    }

    private fun calculateNewSize(size: Int): Int {
        val maxSize = Day.values().size
        return if (size > maxSize) maxSize else size
    }

    private fun createNewListBasedOnSize(size: Int) = Day.values().copyOfRange(0, size)

    private fun calculateNewSelection(size: Int): Int {
        val currentSelection = spinnerDaySelection.selectedItemPosition
        return if (currentSelection >= size) size - 1 else currentSelection
    }

    private fun updateAdapterAndSelection(adapter: SpinnerAdapter, currentSelection: Int) {
        spinnerDaySelection.adapter = adapter
        spinnerDaySelection.setSelection(currentSelection)
    }

    fun safeSelection(selectionIndex: Int) {
        val size = spinnerDaySelection.adapter.count
        val newSelection = if (selectionIndex >= size) size - 1 else selectionIndex
        spinnerDaySelection.setSelection(newSelection)
    }
}
