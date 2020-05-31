package pozzo.apps.travelweather.direction.google

import com.google.android.gms.maps.model.LatLng
import okhttp3.OkHttpClient
import okhttp3.Request
import pozzo.apps.travelweather.BuildConfig
import pozzo.apps.travelweather.core.bugtracker.Bug
import java.io.IOException
import java.net.URL

class GoogleDirectionRequester(private val okHttp: OkHttpClient) {

    fun request(start: LatLng, end: LatLng): String? {
        return try {
            val url = createUrl(start, end)
            val request = Request.Builder().url(url).build()
            okHttp.newCall(request).execute().body()?.string()
        } catch (e: IOException) {
            throw e
        } catch (e: Exception) {
            Bug.get().logException(e)
            null
        }
    }

    private fun createUrl(start: LatLng, end: LatLng) : URL {
        return URL("https://maps.googleapis.com/maps/api/directions/json?"
             + "origin=" + start.latitude + "," + start.longitude
             + "&destination=" + end.latitude + "," + end.longitude
             + "&sensor=false&units=metric&mode=driving"
             + "&key=" + BuildConfig.DIRECTIONS)
    }
}
