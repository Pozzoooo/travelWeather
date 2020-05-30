package pozzo.apps.travelweather.map.manager

import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.ArgumentCaptor
import pozzo.apps.travelweather.forecast.model.Day
import pozzo.apps.travelweather.map.factory.AdapterFactory

class DaySelectionListManagerTest {
    private val arrayAdapter: ArrayAdapter<Day> = mock()
    private val spinnerDaySelection: Spinner = mock {
        on { selectedItemPosition } doReturn 2
        on { context } doReturn mock()
    }
    private val callback: AdapterView.OnItemSelectedListener = mock()
    private val adapterFactory: AdapterFactory = mock {
        on { createArrayAdapter<Day>(any(), any()) } doReturn arrayAdapter
    }

    private val daySelectionListManager =
            DaySelectionListManager(spinnerDaySelection, adapterFactory, callback)

    @Test fun assertLowerArraySize() {
        daySelectionListManager.updateDaySelections(6)

        assertNewArraySize(6)
    }

    private fun assertNewArraySize(size: Int) {
        verify(spinnerDaySelection, times(2)).adapter = arrayAdapter
        verify(spinnerDaySelection, times(2)).setSelection(2)

        val daysCaptor = ArgumentCaptor.forClass(Array<Day>::class.java)
        verify(adapterFactory, times(2)).createArrayAdapter(any(), capture(daysCaptor))
        val days = daysCaptor.lastValue
        assertEquals(size, days.size)
    }

    @Test fun assertMaxArraySize() {
        daySelectionListManager.updateDaySelections(1000)

        assertNewArraySize(12)
    }

    @Test fun assertStandardSelection() {
        whenever(spinnerDaySelection.adapter).thenReturn(arrayAdapter)
        whenever(arrayAdapter.count).thenReturn(12)

        daySelectionListManager.safeSelection(5)

        verify(spinnerDaySelection).setSelection(2)
        verify(spinnerDaySelection).setSelection(5)
    }

    @Test fun assertOverflowSelection() {
        whenever(spinnerDaySelection.adapter).thenReturn(arrayAdapter)
        whenever(arrayAdapter.count).thenReturn(12)

        daySelectionListManager.safeSelection(99)

        verify(spinnerDaySelection).setSelection(2)
        verify(spinnerDaySelection).setSelection(11)
    }
}
