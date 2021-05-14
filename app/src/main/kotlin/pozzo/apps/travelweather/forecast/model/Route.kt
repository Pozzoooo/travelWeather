package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.channels.Channel
import pozzo.apps.travelweather.direction.Direction
import pozzo.apps.travelweather.forecast.model.point.FinishPoint
import pozzo.apps.travelweather.forecast.model.point.StartPoint
import pozzo.apps.travelweather.forecast.model.point.WeatherPoint

class Route(baseRoute: Route? = null,
            startPoint: StartPoint? = null,
            finishPoint: FinishPoint? = null,
            polyline: PolylineOptions? = null,
            weatherLocationCount: Int? = null,
            weatherPoints: Channel<WeatherPoint>? = null,
            direction: Direction? = null) {

    val startPoint: StartPoint?
    val finishPoint: FinishPoint?
    val polyline: PolylineOptions?
    val weatherPoints: Channel<WeatherPoint>
    val weatherLocationCount: Int
    val direction: Direction?

    init {
        this.startPoint = startPoint ?: baseRoute?.startPoint
        this.finishPoint = finishPoint ?: baseRoute?.finishPoint
        this.polyline = polyline ?: baseRoute?.polyline
        this.weatherPoints = weatherPoints ?: baseRoute?.weatherPoints ?: Channel(0)
        this.weatherLocationCount = weatherLocationCount ?: baseRoute?.weatherLocationCount ?: 0
        this.direction = direction ?: baseRoute?.direction
    }

    fun isComplete(): Boolean = startPoint != null && finishPoint != null
    fun isEmpty(): Boolean = startPoint == null && finishPoint == null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Route

        if (startPoint != other.startPoint) return false
        if (finishPoint != other.finishPoint) return false

        return true
    }

    override fun hashCode(): Int {
        var result = startPoint?.hashCode() ?: 0
        result = 31 * result + (finishPoint?.hashCode() ?: 0)
        return result
    }
}
