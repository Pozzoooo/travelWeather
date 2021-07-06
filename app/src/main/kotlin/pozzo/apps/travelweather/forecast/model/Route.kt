package pozzo.apps.travelweather.forecast.model

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.channels.Channel
import pozzo.apps.travelweather.direction.Direction
import pozzo.apps.travelweather.forecast.model.point.*

class Route(baseRoute: Route? = null,
            startPoint: StartPoint? = null,
            finishPoint: FinishPoint? = null,
            waypoints: List<WayPoint>? = null,
            polyline: PolylineOptions? = null,
            weatherLocationCount: Int? = null,
            weatherPoints: Channel<WeatherPoint>? = null,
            direction: Direction? = null): Parcelable {

    val startPoint: StartPoint? = startPoint ?: baseRoute?.startPoint
    val finishPoint: FinishPoint? = finishPoint ?: baseRoute?.finishPoint
    val waypoints: List<WayPoint>? = waypoints ?: baseRoute?.waypoints
    val polyline: PolylineOptions? = polyline ?: baseRoute?.polyline
    val weatherPoints: Channel<WeatherPoint> = weatherPoints ?: baseRoute?.weatherPoints ?: Channel(0)
    val weatherLocationCount: Int = weatherLocationCount ?: baseRoute?.weatherLocationCount ?: 0
    val direction: Direction? = direction ?: baseRoute?.direction

    fun isComplete(): Boolean = startPoint != null && finishPoint != null
    fun isEmpty(): Boolean = startPoint == null && finishPoint == null

    fun getAllPoints(): List<MapPoint> {
        val totalSize = (waypoints?.size ?: 0) + 2
        val allWaypoints = ArrayList<MapPoint>(totalSize)
        startPoint?.let { allWaypoints.add(startPoint) }
        waypoints?.forEach { allWaypoints.add(it) }
        finishPoint?.let { allWaypoints.add(finishPoint) }
        return allWaypoints
    }

    fun getAllPointsPosition(): List<LatLng> {
        return getAllPoints().map { it.position }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Route

        if (startPoint != other.startPoint) return false
        if (finishPoint != other.finishPoint) return false
        if (waypoints != other.waypoints) return false

        return true
    }

    override fun hashCode(): Int {
        var result = startPoint?.hashCode() ?: 0
        result = 31 * result + (finishPoint?.hashCode() ?: 0)
        return result
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        out.writeParcelable(startPoint?.position, flags)
        out.writeParcelable(finishPoint?.position, flags)
        out.writeParcelableArray(parseWaypoints(), flags)
    }

    private fun parseWaypoints(): Array<LatLng> {
        return waypoints?.map {
            it.position
        }?.toTypedArray() ?: emptyArray()
    }

    fun copyRouteAddingWaypoint(latLng: LatLng): Route {
        val waypoint = WayPoint(position = latLng)
        val waypointList = waypoints?.let { ArrayList(it) } ?: ArrayList(1)
        waypointList.add(waypoint)
        return Route(baseRoute = this, waypoints = waypointList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Route> {
        override fun createFromParcel(parcel: Parcel): Route {
            val latLngClassLoader = LatLng::class.java.classLoader
            val startLatLng = parcel.readParcelable<LatLng>(latLngClassLoader)
            val finishLatLng = parcel.readParcelable<LatLng>(latLngClassLoader)
            val waypointsLatLng = parcel.readParcelableArray(latLngClassLoader) as Array<LatLng>

            val startPoint = startLatLng?.let { StartPoint(startLatLng) }
            val finishPoint = finishLatLng?.let { FinishPoint(finishLatLng) }

            return Route(startPoint = startPoint, finishPoint = finishPoint,
                    waypoints = unParseWaypoints(waypointsLatLng))
        }

        private fun unParseWaypoints(waypointsLatLng: Array<LatLng>?): List<WayPoint>? {
            return waypointsLatLng?.map {
                WayPoint(position = it)
            }?.toList()
        }

        override fun newArray(size: Int): Array<Route?> {
            return arrayOfNulls(size)
        }
    }
}
