package pozzo.apps.travelweather.forecast.yahoo

import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.forecast.ForecastType
import pozzo.apps.travelweather.forecast.ForecastTypeMapper
import pozzo.apps.travelweather.forecast.model.Forecast
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
            "windy" to ForecastType.WINDY,
            "breezy" to ForecastType.BREEZY)

    override fun getForecastType(forecast: Forecast) : ForecastType {
        val forecastType = forecastTypeMap[forecast.text?.toLowerCase(Locale.US)]
        return if (forecastType == null) {
            Bug.get().logException("Unknown forecast ${forecast.text}")
            ForecastType.UNKNOWN
        } else {
            forecastType
        }

    }
}
