package pozzo.apps.travelweather.location.google

import com.google.android.gms.maps.model.LatLng
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import pozzo.apps.travelweather.BuildConfig
import pozzo.apps.travelweather.core.bugtracker.Bug
import java.io.IOException
import java.net.URL

class GoogleDirectionRequester(private val okHttp: OkHttpClient) {

    fun request(start: LatLng, end: LatLng): Response? {
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
}
