package pozzo.apps.travelweather.forecast.yahoo

import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import pozzo.apps.travelweather.GsonFactory
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.forecast.ForecastClient
import pozzo.apps.travelweather.forecast.ForecastTypeMapper
import pozzo.apps.travelweather.forecast.model.Forecast
import pozzo.apps.travelweather.forecast.model.Weather
import pozzo.apps.travelweather.map.model.Address
import retrofit2.Response

/**
 * Yahoo forecast api client.
 */
@Deprecated("They say end of life on January 3rd 2019, lets keep an eye on it")
class ForecastClientYahoo(private val yahooWeather: YahooWeather, private val forecastTypeMapper: ForecastTypeMapper) : ForecastClient {
    companion object {
        const val MAX_RETRIES = 3
    }

    override fun fromCoordinates(coordinates: LatLng): Weather? {
        val query = "select item from weather.forecast where woeid in " +
                "(select woeid from geo.places where " +
                "text=\"(" + coordinates.latitude + "," + coordinates.longitude + ")\") and u='c'"
        return requestWeather(query, coordinates)
    }

    private fun requestWeather(query: String, coordinates: LatLng) : Weather? {
        return requestWeather(query, MAX_RETRIES, coordinates)
    }

    private fun requestWeather(query: String, maxRetries: Int, coordinates: LatLng): Weather? {
        val response: Response<ResponseBody>
        try {
            response = yahooWeather.forecast(query).execute()
        } catch (e: RuntimeException) {
            Bug.get().logException("query", query, e)
            return null
        }

        val result = response.body()?.string()
        try {
            if (result != null)
                return handleResponse(result, coordinates)
        } catch (e: Exception) {
            Bug.get().logException("Result", result ?: "", e)
        }

        if (maxRetries > 0)
            return requestWeather(query, maxRetries-1, coordinates)
        return null
    }

    private fun handleResponse(body: String?, coordinates: LatLng) : Weather? {
        val jsonResult = JsonParser().parse(body).asJsonObject

        val result = jsonResult
                .getAsJsonObject("query")
                .getAsJsonObject("results")

        try {
            val channel = result.getAsJsonObject("channel")
            val item = channel.getAsJsonObject("item")
            val forecastArray = item.getAsJsonArray("forecast")
            val forecastType = object : TypeToken<List<Forecast>>() {}.type
            val forecasts = GsonFactory.gson.fromJson<List<Forecast>>(forecastArray, forecastType)
            if (forecasts.isEmpty())
                return null//TODO need to re add the mapper in here

            forecasts.forEach { it.forecastType = forecastTypeMapper.getForecastType(it.text) }
            return Weather(getLink(item), forecasts, Address(coordinates))
        } catch (e: ClassCastException) {
            if (result.isJsonNull) {
                //sometime yahoo is sending us a null object, not really sure why, but all we can do for
                //  now is ignore it and keep on going
                return null
            }
            throw e
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
