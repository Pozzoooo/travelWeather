package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.channels.Channel
import pozzo.apps.travelweather.forecast.model.point.FinishPoint
import pozzo.apps.travelweather.forecast.model.point.MapPoint
import pozzo.apps.travelweather.forecast.model.point.StartPoint

class Route(baseRoute: Route? = null,
            startPoint: StartPoint? = null,
            finishPoint: FinishPoint? = null,
            polyline: PolylineOptions? = null,
            mapPoints: Channel<MapPoint>? = null) {

    val startPoint: StartPoint?
    val finishPoint: FinishPoint?
    val polyline: PolylineOptions?
    val mapPoints: Channel<MapPoint>

    init {
        this.startPoint = startPoint ?: baseRoute?.startPoint
        this.finishPoint = finishPoint ?: baseRoute?.finishPoint
        this.polyline = polyline ?: baseRoute?.polyline
        this.mapPoints = mapPoints ?: baseRoute?.mapPoints ?: Channel(0)
    }

    fun isComplete() : Boolean = startPoint != null && finishPoint != null
    fun isEmpty() : Boolean = startPoint == null && finishPoint == null
}
