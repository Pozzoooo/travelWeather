package pozzo.apps.travelweather.forecast.model

import java.util.*

data class Time(val hour: Int) {

    companion object {
        fun getDefault() =
                Time(GregorianCalendar.getInstance().apply {
                    roll(Calendar.HOUR_OF_DAY, 1)
                }.get(Calendar.HOUR_OF_DAY))
    }

    override fun toString(): String {
        return "$hour:00"
    }
}
