package pozzo.apps.travelweather.forecast.openweather

import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.forecast.ForecastType
import pozzo.apps.travelweather.forecast.ForecastTypeMapper
import java.util.*

class ForecastTypeMapperOpenWeather : ForecastTypeMapper {
    private val forecastTypeMap = mapOf(
            "clear" to ForecastType.SUNNY,
            "rain" to ForecastType.RAIN,
            "clouds" to ForecastType.CLOUDY,
            "snow" to ForecastType.SNOW)

    override fun getForecastType(type: String) : ForecastType {
        val forecastType = forecastTypeMap[type.toLowerCase(Locale.US)]
        return if (forecastType == null) {
            Bug.get().logException("Unknown forecast ${type}")
            ForecastType.UNKNOWN
        } else {
            forecastType
        }
    }
}
