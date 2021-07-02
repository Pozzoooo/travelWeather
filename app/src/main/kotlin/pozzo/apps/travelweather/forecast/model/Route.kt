package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.channels.Channel
import pozzo.apps.travelweather.direction.Direction
import pozzo.apps.travelweather.forecast.model.point.FinishPoint
import pozzo.apps.travelweather.forecast.model.point.StartPoint
import pozzo.apps.travelweather.forecast.model.point.WayPoint
import pozzo.apps.travelweather.forecast.model.point.WeatherPoint

class Route(baseRoute: Route? = null,
            startPoint: StartPoint? = null,
            finishPoint: FinishPoint? = null,
            waypoints: List<WayPoint>? = null,
            polyline: PolylineOptions? = null,
            weatherLocationCount: Int? = null,
            weatherPoints: Channel<WeatherPoint>? = null,
            direction: Direction? = null) {

    val startPoint: StartPoint? = startPoint ?: baseRoute?.startPoint
    val finishPoint: FinishPoint? = finishPoint ?: baseRoute?.finishPoint
    val waypoints: List<WayPoint>? = waypoints ?: baseRoute?.waypoints
    val polyline: PolylineOptions? = polyline ?: baseRoute?.polyline
    val weatherPoints: Channel<WeatherPoint> = weatherPoints ?: baseRoute?.weatherPoints ?: Channel(0)
    val weatherLocationCount: Int = weatherLocationCount ?: baseRoute?.weatherLocationCount ?: 0
    val direction: Direction? = direction ?: baseRoute?.direction

    fun isComplete(): Boolean = startPoint != null && finishPoint != null
    fun isEmpty(): Boolean = startPoint == null && finishPoint == null

    fun getAllWaypoints(): List<LatLng> {
        val totalSize = waypoints?.size ?: 0 + 2
        val allWaypoints = ArrayList<LatLng>(totalSize)
        startPoint?.let { allWaypoints.add(startPoint.position) }
        waypoints?.forEach { allWaypoints.add(it.position) }
        finishPoint?.let { allWaypoints.add(finishPoint.position) }
        return allWaypoints
    }

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
