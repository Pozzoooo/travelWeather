package pozzo.apps.travelweather.direction

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.analytics.MapAnalytics
import kotlin.math.abs

/**
 * The idea here is to pick where the weathers are gonna be shown in the direction line.
 */
class DirectionWeatherFilter(private val mapAnalytics: MapAnalytics) {
    companion object {
        private const val PADDING = 350
        private const val MIN_SIZE = 1300
        private const val MEDIUM_DIRECTION_THRESHOLD = 12000
        private const val LONG_DIRECTION_THRESHOLD = 35000
        private const val SUPER_LONG_DIRECTION_THRESHOLD = 70000
    }

    private lateinit var directionLine: List<LatLng>

    fun getWeatherPointsLocations(directionLine: List<LatLng>): List<LatLng> {
        this.directionLine = directionLine

        val size = directionLine.size
        return when {
            0 == size -> emptyDirectionLine()
            MIN_SIZE > size -> createShortDirectionList()
            MEDIUM_DIRECTION_THRESHOLD > size -> creteWeatherPoints(.5)
            LONG_DIRECTION_THRESHOLD > size -> creteWeatherPoints(1.5)
            SUPER_LONG_DIRECTION_THRESHOLD > size -> creteWeatherPoints(2.5)
            else -> creteWeatherPoints(7.0)
        }
    }

    private fun emptyDirectionLine(): List<LatLng> {
        mapAnalytics.sendEmptyForecastCountByRoute()
        return emptyList()
    }

    private fun createShortDirectionList(): List<LatLng> {
        mapAnalytics.sendSingleForecastCountByRoute(directionLine.size)
        return listOf(meanPoint())
    }

    private fun meanPoint(): LatLng = directionLine[directionLine.size / 2]

    private fun creteWeatherPoints(minDistance: Double): List<LatLng> {
        val weatherPoints = mutableListOf(startPoint())
        var lastForecast = weatherPoints[0]
        for (i in 600 until directionLine.size - 700 step 250) {
            val latLng = directionLine[i]
            if (isMinDistanceToForecast(latLng, lastForecast, minDistance)) {
                lastForecast = latLng
                weatherPoints.add(latLng)
            }
        }
        weatherPoints.add(lastPoint())
        mapAnalytics.sendForecastCountByRoute(weatherPoints.size, directionLine.size)
        return weatherPoints
    }

    private fun startPoint(): LatLng = directionLine[PADDING]
    private fun lastPoint(): LatLng = directionLine[directionLine.size - PADDING]

    fun isMinDistanceToForecast(from: LatLng, to: LatLng, minDistance: Double): Boolean {
        val distance = abs(from.latitude - to.latitude) + abs(from.longitude - to.longitude)
        return distance > minDistance
    }
}
