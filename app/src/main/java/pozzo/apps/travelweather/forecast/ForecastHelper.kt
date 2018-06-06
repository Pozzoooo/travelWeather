package pozzo.apps.travelweather.forecast

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.splunk.mint.Mint

import java.util.Locale

import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.forecast.model.Forecast

/**
 * To help us on reusability, as I am not sure exactly where to place it =/.
 */
object ForecastHelper {
    private const val SUNNY = "sunny"
    private const val MOSTLY_SUNNY = "mostly sunny"
    private const val THUNDERSTORMS = "thunderstorms"
    private const val SCATTERED_THUNDERSTORMS = "scattered thunderstorms"
    private const val RAIN = "rain"
    private const val SCATTERED_SHOWERS = "scattered showers"
    private const val SHOWERS = "showers"
    private const val PARTLY_CLOUDY = "partly cloudy"
    private const val MOSTLY_CLOUDY = "mostly cloudy"
    private const val CLOUDY = "cloudy"
    private const val SNOW = "snow"
    private const val RAIN_SNOW = "rain and snow"
    private const val BREEZY = "breezy"

    private val iconIdMap : Map<String, Int>
    private val bitmapCache = HashMap<String?, BitmapDescriptor?>()

    init {
        iconIdMap = mapOf(
                Pair(SUNNY, R.drawable.sun),
                Pair(MOSTLY_SUNNY, R.drawable.mostly_sunny),
                Pair(THUNDERSTORMS, R.drawable.thunderstorm),
                Pair(SCATTERED_THUNDERSTORMS, R.drawable.thunderstorms_scattered),
                Pair(RAIN, R.drawable.rain),
                Pair(SHOWERS, R.drawable.showers),
                Pair(SCATTERED_SHOWERS, R.drawable.rain_scattered),
                Pair(PARTLY_CLOUDY, R.drawable.mostly_sunny),
                Pair(CLOUDY, R.drawable.cloudy),
                Pair(MOSTLY_CLOUDY, R.drawable.mostly_cloudy),
                Pair(SNOW, R.drawable.snow),
                Pair(RAIN_SNOW, R.drawable.snow),
                Pair(BREEZY, R.drawable.cloudy_moon))
    }

    fun forecastIcon(forecast: Forecast): BitmapDescriptor? {
        val forecastText = forecast.text?.toLowerCase(Locale.US) ?: ""
        var bitmap = bitmapCache[forecastText]
        if (bitmap == null) {
            bitmap = createBitmap(forecastText)
            bitmapCache[forecastText] = bitmap
        }

        return bitmap
    }

    private fun createBitmap(forecastText: String?) : BitmapDescriptor? {
        var icon = iconIdMap[forecastText]
        if (icon == null) {
            icon = R.drawable.cloudy_moon
            Mint.logException(Exception("Couldn't find icon for $forecastText"))
        }
        return BitmapDescriptorFactory.fromResource(icon)
    }

    /**
     * @return true if distance is enough for a new forecast.
     */
    fun isMinDistanceToForecast(from: LatLng, to: LatLng): Boolean {
        val distance = Math.abs(from.latitude - to.latitude) + Math.abs(from.longitude - to.longitude)
        return distance > 0.5
    }
}
