package pozzo.apps.travelweather.location.google

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import pozzo.apps.travelweather.BuildConfig
import pozzo.apps.travelweather.core.bugtracker.Bug
import java.io.IOException
import java.net.URL
import java.util.*

class GMapV2Direction(private val okHttp: OkHttpClient, private val gson: Gson) {

    fun getDirection(start: LatLng, end: LatLng) : List<LatLng>? {
        val response = makeRequest(start, end)
        return parseResponse(response)?.let {
            decodePoly(it.routes[0].legs[0].steps[0].polyline.points)
        }
    }

    private fun parseResponse(response: Response?) : GoogleDirectionResponse? {
        val body = response?.body()?.string() ?: return null
        return gson.fromJson(body, GoogleDirectionResponse::class.java)
    }

    private fun makeRequest(start: LatLng, end: LatLng): Response? {
        try {
            val url = URL("https://maps.googleapis.com/maps/api/directions/json?"
                    + "origin=" + start.latitude + "," + start.longitude
                    + "&destination=" + end.latitude + "," + end.longitude
                    + "&sensor=false&units=metric&mode=driving"
                    + "&key=" + BuildConfig.DIRECTIONS)

            val request = Request.Builder().url(url).build()
            return okHttp.newCall(request).execute()
        } catch (e: IOException) {
            throw e
        } catch (e: Exception) {
            Bug.get().logException(e)
        }

        return null
    }

    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val position = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(position)
        }
        return poly
    }
}
