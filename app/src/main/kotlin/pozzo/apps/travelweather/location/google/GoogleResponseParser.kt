package pozzo.apps.travelweather.location.google

import com.google.gson.Gson
import okhttp3.Response

class GoogleResponseParser(private val gson: Gson) {

    fun parse(response: Response) : GoogleDirectionResponse? {
        val body = response.body()?.string() ?: return null
        return gson.fromJson(body, GoogleDirectionResponse::class.java)
    }
}
