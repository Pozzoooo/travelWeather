package pozzo.apps.travelweather.forecast.model

import pozzo.apps.travelweather.App
import pozzo.apps.travelweather.R
import java.text.DateFormat
import java.util.*

//TODO refactor, this is quite messy at the moment
private fun getEnumString(stringId: Int) = App.component().app().getString(stringId)

private fun getEnumDate(index: Int): String {
    val date = Calendar.getInstance()
    date.roll(Calendar.DAY_OF_YEAR, index)
    return DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(date.time)
}

enum class Day(val index: Int, val title: String) {
    TODAY(0, getEnumString(R.string.day_today)),
    TOMORROW(1, getEnumString(R.string.day_tomorrow)),
    AFTER_TOMORROW(2, getEnumDate(2)),
    IDX_3(3, getEnumDate(3)),
    IDX_4(4, getEnumDate(4)),
    IDX_5(5, getEnumDate(5)),
    IDX_6(6, getEnumDate(6)),
    IDX_7(7, getEnumDate(7)),
    IDX_8(8, getEnumDate(8)),
    IDX_9(9, getEnumDate(9));

    override fun toString() = title

    companion object {
        fun getByIndex(index: Int): Day =
                Day.values().firstOrNull {
                    it.index == index
                }?.let { it } ?: TODAY
    }
}
