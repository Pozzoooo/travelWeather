package pozzo.apps.travelweather.forecast.yahoo

import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import pozzo.apps.travelweather.GsonFactory
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.forecast.ForecastClient
import pozzo.apps.travelweather.forecast.model.Forecast
import pozzo.apps.travelweather.forecast.model.Weather
import pozzo.apps.travelweather.map.model.Address
import retrofit2.Response
import java.lang.ClassCastException
import java.lang.RuntimeException

/**
 * Yahoo forecast api client.
 */
class ForecastClientYahoo(private val yahooWeather: YahooWeather) : ForecastClient {
    companion object {
        const val MAX_RETRIES = 3
    }

    override fun fromCoordinates(coordinates: LatLng): Weather? {
        val query = "select item from weather.forecast where woeid in " +
                "(select woeid from geo.places where " +
                "text=\"(" + coordinates.latitude + "," + coordinates.longitude + ")\") and u='c'"
        return requestWeather(query)?.apply {
            val address = Address()
            address.latitude = coordinates.latitude
            address.longitude = coordinates.longitude
            this.address = address
        }
    }

    private fun requestWeather(query: String) : Weather? {
        return requestWeather(query, MAX_RETRIES)
    }

    private fun requestWeather(query: String, maxRetries: Int): Weather? {
        val response: Response<ResponseBody>
        try {
            response = yahooWeather.forecast(query).execute()
        } catch (e: RuntimeException) {
            Bug.get().logException("query", query, e)
            return null
        }

        val result = response.body()?.string()
        try {
            return handleResponse(result)
        } catch (e: ClassCastException) {
            if (maxRetries > 0)
                return requestWeather(query, maxRetries-1)
            Bug.get().logEvent("Json null")
            return null
        }
    }

    private fun handleResponse(body: String?) : Weather? {
        try {
            val jsonResult = JsonParser().parse(body).asJsonObject
            val channel = jsonResult
                    .getAsJsonObject("query")
                    .getAsJsonObject("results")
                    .getAsJsonObject("channel")
            val item = channel.getAsJsonObject("item")
            val forecastArray = item.getAsJsonArray("forecast")
            val gson = GsonFactory.getGson()
            val forecastType = object : TypeToken<List<Forecast>>() {}.type
            val forecasts = gson.fromJson<List<Forecast>>(forecastArray, forecastType)
            if (forecasts.isEmpty())
                return null

            val weather = Weather()
            weather.setForecasts(forecasts)
            weather.url = getLink(item)
            return weather
        } catch (e: ClassCastException) {
            //sometime yahoo is sending us a null object, not really sure why, but all we can do for
            //  now is ignore it and keep on going
            return null
        }
    }

    private fun getLink(jsonObject: JsonObject) : String {
        val link = jsonObject.get("link").asString
        val separatorIndex = link?.indexOf("*") ?: 0
        return if (separatorIndex > 0) {
            link.substring(separatorIndex + 1)
        } else {
            link
        }
    }
}
