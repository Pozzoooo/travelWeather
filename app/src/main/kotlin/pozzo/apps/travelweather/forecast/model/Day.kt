package pozzo.apps.travelweather.forecast.model

import pozzo.apps.travelweather.App
import pozzo.apps.travelweather.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

enum class Day(val index: Int, val title: Int? = null) {
    TODAY(0, R.string.day_today),
    TOMORROW(1, R.string.day_tomorrow),
    AFTER_TOMORROW(2),
    IDX_3(3),
    IDX_4(4),
    IDX_5(5),
    IDX_6(6);//TODO need to make it dynamic
//    IDX_7(7);

    override fun toString() = title?.let { getTitleFromStringResource(it) } ?: getTitleFromDate(index)

    private fun getTitleFromStringResource(stringId: Int) = App.component().app().getString(stringId)

    private fun getTitleFromDate(index: Int): String {
        val date = App.component().currentDate()
        date.add(Calendar.DAY_OF_YEAR, index)
        return if (index < 7) {
            SimpleDateFormat("EEEE", Locale.getDefault()).format(date.time)
        } else {
            DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(date.time)
        }
    }

    companion object {
        fun getByIndex(index: Int): Day =
                Day.values().firstOrNull {
                    it.index == index
                }?.let { it } ?: TODAY
    }
}
