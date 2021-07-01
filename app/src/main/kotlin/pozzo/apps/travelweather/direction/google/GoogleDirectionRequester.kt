package pozzo.apps.travelweather.direction.google

import com.google.android.gms.maps.model.LatLng
import okhttp3.OkHttpClient
import okhttp3.Request
import pozzo.apps.travelweather.BuildConfig
import pozzo.apps.travelweather.core.bugtracker.Bug
import java.io.IOException
import java.net.URL

/**
 * https://developers.google.com/maps/documentation/directions/get-directions
 *
 * Requests using more than 10 waypoints (between 11 and 25), or waypoint optimization,
 *          are billed at a higher rate
 *
 * TODO URL limit of 8192 characters
 */
class GoogleDirectionRequester(private val okHttp: OkHttpClient) {

    companion object {
        private const val WAYPOINTS_SEPARATOR = "|"
        private const val COORDINATE_SEPARATOR = ","
    }

    fun request(start: LatLng, end: LatLng): String? {
        return request(listOf(start, end))
    }

    private fun request(waypoints: List<LatLng>): String? {
        if (waypoints.size < 2) return null

        return try {
            val url = createUrl(waypoints)
            val request = Request.Builder().url(url).build()
            okHttp.newCall(request).execute().body()?.string()
        } catch (e: IOException) {
            throw e
        } catch (e: Exception) {
            Bug.get().logException(e)
            null
        }
    }

    //TODO departure_time
    private fun createUrl(waypoints: List<LatLng>) : URL {
        val origin = "origin=" + waypoints.first().latitude +
                COORDINATE_SEPARATOR + waypoints.first().longitude
        val destination = "destination=" + waypoints.last().latitude +
                COORDINATE_SEPARATOR + waypoints.last().longitude

        var urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
                origin + "&" + destination +
                "&sensor=false&units=metric&mode=driving" +
                "&key=" + BuildConfig.DIRECTIONS

        createIntermediateWayPoints(waypoints)?.let {
            urlString += "&$it"
        }

        return URL(urlString)
    }

    private fun createIntermediateWayPoints(waypoints: List<LatLng>): String? {
        if (waypoints.size <= 2) return null

        val intermediate = waypoints.subList(1, waypoints.lastIndex)//TODO Validar range
        var intermediateUrlParameter = "waypoints="

        intermediate.forEach {
            intermediateUrlParameter += "${it.latitude},${it.longitude}$WAYPOINTS_SEPARATOR"
        }

        val suffixPosition = intermediateUrlParameter.length - WAYPOINTS_SEPARATOR.length
        return intermediateUrlParameter.substring(0, suffixPosition)
    }
}
