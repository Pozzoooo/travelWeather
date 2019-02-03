package pozzo.apps.travelweather.forecast.weatherunlocked

import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.forecast.ForecastType
import pozzo.apps.travelweather.forecast.ForecastTypeMapper
import java.util.*

/**
 * https://developer.weatherunlocked.com/documentation/localweather/resources
 */
class ForecastTypeMapperWeatherUnlocked : ForecastTypeMapper {
    private val forecastTypeMap = mapOf(
            "clear.gif" to ForecastType.SUNNY,
            "sunny.gif" to ForecastType.SUNNY,
            "mostly sunny" to ForecastType.MOSTLY_SUNNY,
            "partcloudrainthundernight.gif" to ForecastType.THUNDERSTORMS,
            "partcloudrainthunderday.gif" to ForecastType.THUNDERSTORMS,
            "cloudrainthunder.gif" to ForecastType.THUNDERSTORMS,
            "scattered thunderstorms" to ForecastType.SCATTERED_THUNDERSTORMS,
            "occlightsleet.gif" to ForecastType.SLEET,
            "modsleetswrsday.gif" to ForecastType.SLEET,
            "modsleetswrsnight.gif" to ForecastType.SLEET,
            "modsleet.gif" to ForecastType.SLEET,
            "isosleetswrsnight.gif" to ForecastType.SLEET,
            "isosleetswrsday.gif" to ForecastType.SLEET,
            "heavysleetswrsnight.gif" to ForecastType.SLEET,
            "heavysleetswrsday.gif" to ForecastType.SLEET,
            "heavysleet.gif" to ForecastType.SLEET,
            "freezingrain.gif" to ForecastType.SCATTERED_THUNDERSTORMS,
            "cloudsleetsnowthunder.gif" to ForecastType.SCATTERED_THUNDERSTORMS,
            "modrain.gif" to ForecastType.RAIN,
            "modrainswrsnight.gif" to ForecastType.RAIN,
            "heavyrainswrsnight.gif" to ForecastType.RAIN,
            "heavyrainswrsday.gif" to ForecastType.RAIN,
            "heavyrain.gif" to ForecastType.RAIN,
            "modrainswrsday.gif" to ForecastType.SHOWERS,
            "freezingdrizzle.gif" to ForecastType.SHOWERS,
            "occlightrain.gif" to ForecastType.SCATTERED_SHOWERS,
            "isorainswrsday.gif" to ForecastType.SCATTERED_SHOWERS,
            "isorainswrsnight.gif" to ForecastType.SCATTERED_SHOWERS,
            "partlycloudynight.gif" to ForecastType.PARTLY_CLOUDY,
            "partlycloudyday.gif" to ForecastType.PARTLY_CLOUDY,
            "overcast.gif" to ForecastType.CLOUDY,
            "cloudy.gif" to ForecastType.CLOUDY,
            "mostly cloudy" to ForecastType.MOSTLY_CLOUDY,
            "modsnow.gif" to ForecastType.SNOW,
            "modsnowswrsday.gif" to ForecastType.SNOW,
            "modsnowswrsnight.gif" to ForecastType.SNOW,
            "isosnowswrsnight.gif" to ForecastType.SNOW,
            "isosnowswrsday.gif" to ForecastType.SNOW,
            "partcloudsleetsnowthundernight.gif" to ForecastType.SNOW,
            "partcloudsleetsnowthunderday.gif" to ForecastType.SNOW,
            "occlightsnow.gif" to ForecastType.SNOW,
            "heavysnowswrsday.gif" to ForecastType.SNOW,
            "blizzard.gif" to ForecastType.RAIN_SNOW,
            "heavysnowswrsnight.gif" to ForecastType.RAIN_SNOW,
            "heavysnow.gif" to ForecastType.RAIN_SNOW,
            "windy" to ForecastType.WINDY,
            "fog.gif" to ForecastType.FOG,
            "mist.gif" to ForecastType.FOG,
            "freezingfog.gif" to ForecastType.FOG,
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
