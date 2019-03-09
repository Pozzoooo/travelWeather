package pozzo.apps.travelweather.map.manager

import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import pozzo.apps.travelweather.forecast.model.Day

class DaySelectionListManager(private val spinnerDaySelection: Spinner, callback: AdapterView.OnItemSelectedListener) {
    companion object {
        private const val DEFAULT_SIZE = 7
    }

    init {
        updateDaySelections(DEFAULT_SIZE)
        spinnerDaySelection.onItemSelectedListener = callback
    }

    fun updateDaySelections(size: Int) {
        val newSize = calculateNewSize(size)
        val adapter = ArrayAdapter<Day>(spinnerDaySelection.context,
                android.R.layout.simple_list_item_1, createNewListBasedOnSize(newSize))
        updateAdapterAndSelection(adapter, calculateNewSelection(newSize))
    }

    private fun calculateNewSize(size: Int): Int {
        val maxSize = Day.values().size
        return if (size > maxSize) maxSize else size
    }

    private fun createNewListBasedOnSize(size: Int) = Day.values().copyOfRange(0, size)

    private fun calculateNewSelection(size: Int) : Int {
        val currentSelection = spinnerDaySelection.selectedItemPosition
        return if (currentSelection >= size) size - 1 else currentSelection
    }

    private fun updateAdapterAndSelection(adapter: SpinnerAdapter, currentSelection: Int) {
        spinnerDaySelection.adapter = adapter
        spinnerDaySelection.setSelection(currentSelection)
    }

    fun safeSelection(selectionIndex: Int) {
        val max = spinnerDaySelection.adapter.count
        val newSelection = if (selectionIndex> max) max - 1 else selectionIndex
        spinnerDaySelection.setSelection(newSelection)
    }
}
