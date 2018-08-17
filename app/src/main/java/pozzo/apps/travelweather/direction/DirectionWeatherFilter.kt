package pozzo.apps.travelweather.direction

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.forecast.ForecastBusiness

/**
 * The idea here is to pick where the weathers are gonna be shown in the direction line.
 */
class DirectionWeatherFilter(private val forecastBusiness: ForecastBusiness) {
    companion object {
        private const val MIN_SIZE = 1000
        private const val PADDING = 350
    }

    private lateinit var directionLine : List<LatLng>

    fun getWeatherPointsLocations(directionLine: List<LatLng>) : List<LatLng> {
        this.directionLine = directionLine

        return when {
            directionLine.isEmpty() -> emptyList()
            isShortDirection() -> createShortDirectionList()
            else -> createLongDirectionList()
        }
    }

    private fun isShortDirection() : Boolean = directionLine.size < MIN_SIZE
    private fun createShortDirectionList() = listOf(meanPoint())
    private fun meanPoint() : LatLng = directionLine[directionLine.size / 2]

    private fun createLongDirectionList() : List<LatLng> {
        val filteredPoints = mutableListOf<LatLng>()
        addStartAndFinish(filteredPoints)
        addMiddlePoints(filteredPoints)
        return filteredPoints
    }

    private fun addStartAndFinish(filteredPoints: MutableList<LatLng>) {
        filteredPoints.add(startPoint())
        filteredPoints.add(lastPoint())
    }

    private fun startPoint() : LatLng = directionLine[PADDING]
    private fun lastPoint() : LatLng = directionLine[directionLine.size - PADDING]

    private fun addMiddlePoints(filteredPoints: MutableList<LatLng>) {
        var lastForecast = filteredPoints[0]
        for (i in 500 until directionLine.size - 500) {
            val latLng = directionLine[i]
            if (isGoodFitForWeather(i, latLng, lastForecast)) {
                lastForecast = latLng
                filteredPoints.add(latLng)
            }
        }
    }

    private fun isGoodFitForWeather(position: Int, latLng: LatLng, lastForecast: LatLng) : Boolean {
        return position % 250 == 1 //Um mod para nao checar em todos os pontos, sao muitos
                && forecastBusiness.isMinDistanceToForecast(latLng, lastForecast)
    }
}
