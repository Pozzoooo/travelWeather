package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.channels.Channel
import pozzo.apps.travelweather.forecast.model.point.FinishPoint
import pozzo.apps.travelweather.forecast.model.point.StartPoint
import pozzo.apps.travelweather.forecast.model.point.WeatherPoint

class Route(baseRoute: Route? = null,
            startPoint: StartPoint? = null,
            finishPoint: FinishPoint? = null,
            polyline: PolylineOptions? = null,
            weatherLocationCount: Int? = null,
            weatherPoints: Channel<WeatherPoint>? = null) {

    val startPoint: StartPoint?
    val finishPoint: FinishPoint?
    val polyline: PolylineOptions?
    val weatherPoints: Channel<WeatherPoint>
    val weatherLocationCount: Int

    init {
        this.startPoint = startPoint ?: baseRoute?.startPoint
        this.finishPoint = finishPoint ?: baseRoute?.finishPoint
        this.polyline = polyline ?: baseRoute?.polyline
        this.weatherPoints = weatherPoints ?: baseRoute?.weatherPoints ?: Channel(0)
        this.weatherLocationCount = weatherLocationCount ?: baseRoute?.weatherLocationCount ?: 0
    }

    fun isComplete() : Boolean = startPoint != null && finishPoint != null
    fun isEmpty() : Boolean = startPoint == null && finishPoint == null
}
