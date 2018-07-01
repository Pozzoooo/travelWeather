package pozzo.apps.travelweather.forecast.yahoo

import com.splunk.mint.Mint
import pozzo.apps.travelweather.forecast.ForecastType
import pozzo.apps.travelweather.forecast.ForecastTypeMapper
import pozzo.apps.travelweather.forecast.model.Forecast

class ForecastTypeMapperYahoo : ForecastTypeMapper {
    private val forecastTypeMap = mapOf(
            "Sunny" to ForecastType.SUNNY,
            "Mostly Sunny" to ForecastType.MOSTLY_SUNNY,
            "Thunderstorms" to ForecastType.THUNDERSTORMS,
            "Scattered Thunderstorms" to ForecastType.SCATTERED_THUNDERSTORMS,
            "Rain" to ForecastType.RAIN,
            "Showers" to ForecastType.SHOWERS,
            "Scattered Showers" to ForecastType.SCATTERED_SHOWERS,
            "Partly Cloudy" to ForecastType.PARTLY_CLOUDY,
            "Cloudy" to ForecastType.CLOUDY,
            "Mostly Cloudy" to ForecastType.MOSTLY_CLOUDY,
            "Snow" to ForecastType.SNOW,
            "Rain and Snow" to ForecastType.RAIN_SNOW,
            "Windy" to ForecastType.WINDY,
            "Breezy" to ForecastType.BREEZY)

    override fun getForecastType(forecast: Forecast) : ForecastType {
        val forecastType = forecastTypeMap[forecast.text]
        return if (forecastType == null) {
            Mint.logException(Exception("Unknown forecast ${forecast.text}"))
            ForecastType.UNKNOWN
        } else {
            forecastType
        }

    }
}