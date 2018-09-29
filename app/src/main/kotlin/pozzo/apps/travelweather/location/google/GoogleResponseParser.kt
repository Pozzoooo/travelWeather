package pozzo.apps.travelweather.location.google

import com.google.gson.Gson

class GoogleResponseParser(private val gson: Gson) {

    fun parse(response: String) : GoogleDirectionResponse? {
        return gson.fromJson(response, GoogleDirectionResponse::class.java)
    }
}
