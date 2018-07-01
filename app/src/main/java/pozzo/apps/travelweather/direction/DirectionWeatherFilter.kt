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

  private var directionLine: List<LatLng> = emptyList()
  private var filteredPoints: ArrayList<LatLng> = ArrayList()

  fun getWeatherPointsLocations(directionLine: List<LatLng>) : List<LatLng> {
    if (directionLine.isEmpty()) return emptyList()
    initialState(directionLine)

    if (isShortDirection()) {
      filteredPoints.add(meanPoint())
      return filteredPoints
    }

    val firstPoint = startPoint()
    filteredPoints.add(firstPoint)
    filteredPoints.add(lastPoint())

    var lastForecast = firstPoint
    for (i in 500 until directionLine.size - 500) {
      val latLng = directionLine[i]
      if (isGoodFitForWeather(i, latLng, lastForecast)) {
        lastForecast = latLng
        filteredPoints.add(latLng)
      }
    }
    return filteredPoints
  }

  private fun initialState(directionLine: List<LatLng>) {
    this.filteredPoints = ArrayList<LatLng>()
    this.directionLine = directionLine
  }

  private fun isShortDirection() : Boolean = directionLine.size < MIN_SIZE
  private fun meanPoint() : LatLng = directionLine[directionLine.size / 2]
  private fun startPoint() : LatLng = directionLine[PADDING]
  private fun lastPoint() : LatLng = directionLine[directionLine.size - PADDING]

  private fun isGoodFitForWeather(position: Int, latLng: LatLng, lastForecast: LatLng) : Boolean {
    return position % 250 == 1 //Um mod para nao checar em todos os pontos, sao muitos
        && forecastBusiness.isMinDistanceToForecast(latLng, lastForecast)
  }
}
