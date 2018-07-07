package pozzo.apps.travelweather.core

import com.google.gson.Gson

object JsonParser {

    fun <T> fromJson(classOf: Class<T>, json: String) : T = Gson().fromJson(json, classOf)
}
