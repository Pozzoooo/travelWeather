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
    private const val SUN = "sun"
    private const val THUNDERSTORMS = "thunderstorms"
    private const val RAIN = "rain"
    private const val SHOWERS = "showers"
    private const val PARTLY_CLOUDY = "partly cloudy"
    private const val CLOUDY = "cloudy"
    private const val SNOW = "snow"

    private val iconIdMap : Map<String, Int>
    private val bitmapCache = HashMap<String?, BitmapDescriptor?>()

    init {
        iconIdMap = mapOf(
                Pair(SUN, R.drawable.sun),
                Pair(THUNDERSTORMS, R.drawable.thunderstorm),
                Pair(RAIN, R.drawable.heavy_rain),
                Pair(SHOWERS, R.drawable.rain),
                Pair(PARTLY_CLOUDY, R.drawable.partly_cloudy),
                Pair(CLOUDY, R.drawable.cloudy),
                Pair(SNOW, R.drawable.snow))
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
