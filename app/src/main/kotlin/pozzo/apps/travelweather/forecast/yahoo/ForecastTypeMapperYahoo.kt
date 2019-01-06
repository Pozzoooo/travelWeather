package pozzo.apps.travelweather.forecast.yahoo

import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.forecast.ForecastType
import pozzo.apps.travelweather.forecast.ForecastTypeMapper
import java.util.*

class ForecastTypeMapperYahoo : ForecastTypeMapper {
    private val forecastTypeMap = mapOf(
            "sunny" to ForecastType.SUNNY,
            "mostly sunny" to ForecastType.MOSTLY_SUNNY,
            "thunderstorms" to ForecastType.THUNDERSTORMS,
            "scattered thunderstorms" to ForecastType.SCATTERED_THUNDERSTORMS,
            "rain" to ForecastType.RAIN,
            "showers" to ForecastType.SHOWERS,
            "scattered showers" to ForecastType.SCATTERED_SHOWERS,
            "partly cloudy" to ForecastType.PARTLY_CLOUDY,
            "cloudy" to ForecastType.CLOUDY,
            "mostly cloudy" to ForecastType.MOSTLY_CLOUDY,
            "snow" to ForecastType.SNOW,
            "rain and snow" to ForecastType.RAIN_SNOW,
            "snow showers" to ForecastType.RAIN_SNOW,
            "windy" to ForecastType.WINDY,
            "breezy" to ForecastType.BREEZY)

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
