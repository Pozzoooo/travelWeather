package pozzo.apps.travelweather.forecast.weatherunlocked

import com.google.gson.JsonObject
import java.text.SimpleDateFormat
import java.util.*

class DateParserWeatherUnlocked {
    private val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)

    fun parse(timeframe: JsonObject): Calendar {
        val dateString = timeframe.get("date").asString
        val time = timeframe.get("time").asInt
        val hour = time / 100

        val date = formatter.parse(dateString)
        val calendar = GregorianCalendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, hour)

        return calendar
    }
}
