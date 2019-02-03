package pozzo.apps.travelweather.forecast

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.android.gms.maps.model.BitmapDescriptor
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.common.android.BitmapCreator

enum class ForecastType(@DrawableRes val iconId: Int, @StringRes val stringId: Int) {
    SUNNY(                  R.drawable.sun,                     R.string.forecast_sunny),
    MOSTLY_SUNNY(           R.drawable.mostly_sunny,            R.string.forecast_mostlySunny),
    THUNDERSTORMS(          R.drawable.thunderstorm,            R.string.forecast_thunderstorms),
    SCATTERED_THUNDERSTORMS(R.drawable.thunderstorms_scattered, R.string.forecast_scatteredThunderstorms),
    RAIN(                   R.drawable.rain,                    R.string.forecast_rain),
    SHOWERS(                R.drawable.showers,                 R.string.forecast_showers),
    SCATTERED_SHOWERS(      R.drawable.rain_scattered,          R.string.forecast_scatteredShowers),
    PARTLY_CLOUDY(          R.drawable.mostly_cloudy,           R.string.forecast_partlyCloudy),
    CLOUDY(                 R.drawable.cloudy,                  R.string.forecast_cloudy),
    MOSTLY_CLOUDY(          R.drawable.mostly_cloudy,           R.string.forecast_mostlyCloudy),
    SNOW(                   R.drawable.snow,                    R.string.forecast_snow),
    RAIN_SNOW(              R.drawable.rain_snow,               R.string.forecast_rainAndSnow),
    WINDY(                  R.drawable.wind,                    R.string.forecast_windy),
    BREEZY(                 R.drawable.wind,                    R.string.forecast_breezy),
    FOG(                    R.drawable.fog,                     R.string.forecast_fog),
    SLEET(                  R.drawable.sleet,                   R.string.forecast_sleet),
    UNKNOWN(                R.drawable.cloudy_moon,             R.string.forecast_unknown);

    private val bitmapCache = HashMap<ForecastType, BitmapDescriptor?>()

    fun getIcon() : BitmapDescriptor? = bitmapCache[this] ?: createIcon()

    private fun createIcon() : BitmapDescriptor? {
        val icon = BitmapCreator.get().fromResource(iconId)
        bitmapCache[this] = icon
        return icon
    }
}
