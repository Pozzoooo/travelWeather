package pozzo.apps.travelweather.forecast.model

import java.util.*

data class DayTime(val day: Day, val time: Time) {

    fun toCalendar(): Calendar {
        return GregorianCalendar.getInstance().apply {
            roll(Calendar.DAY_OF_YEAR, day.index)
            set(Calendar.HOUR_OF_DAY, this@DayTime.time.hour)
        }
    }
}
