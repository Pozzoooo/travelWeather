package pozzo.apps.travelweather.forecast.yahoo

import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.splunk.mint.Mint
import okhttp3.ResponseBody
import pozzo.apps.travelweather.forecast.ForecastClient
import pozzo.apps.travelweather.GsonFactory
import pozzo.apps.travelweather.map.model.Address
import pozzo.apps.travelweather.forecast.model.Forecast
import pozzo.apps.travelweather.forecast.model.Weather
import retrofit2.Response
import java.lang.ClassCastException
import java.lang.RuntimeException

/**
 * Yahoo forecast api client.
 *
 * @since 12/08/17.
 */
class ForecastClientYahoo : ForecastClient {
    private val MAX_RETRIES = 3

    /**
     * Forecast from given location.
     */
    override fun fromAddress(address: String): Weather? {
        //item = condition + forecast
        //and u='c' - Serve para pegar temperatura em celsius
        val query = "select item from weather.forecast where woeid in " +
                "(select woeid from geo.places(1) where text=\"" + address + "\") and u='c'"
        return requestWeather(query)
    }

    override fun fromCoordinates(coordinates: LatLng): Weather? {
        val query = "select item from weather.forecast where woeid in " +
                "(select woeid from geo.places where " +
                "text=\"(" + coordinates.latitude + "," + coordinates.longitude + ")\") and u='c'"
        val weather = requestWeather(query)
        if (weather != null) {
            val address = Address()
            address.latitude = coordinates.latitude
            address.longitude = coordinates.longitude
            weather.address = address
        }
        return weather
    }

    private fun requestWeather(query: String) : Weather? {
        return requestWeather(query, MAX_RETRIES)
    }

    private fun requestWeather(query: String, maxRetries: Int): Weather? {
        val response: Response<ResponseBody>
        try {
            response = ApiFactory.getInstance().yahooWather.forecast(query).execute()
        } catch (e: RuntimeException) {
            e.printStackTrace()
            Mint.logExceptionMessage("query", query, e)
            throw e
        }

        val result = response.body()?.string()
        try {
            return handleResponse(result)
        } catch (e: ClassCastException) {
            if (maxRetries > 1)
                return requestWeather(query, maxRetries-1)

            Mint.logExceptionMessage("result", result, e)
            throw e
        }
    }

    private fun handleResponse(body: String?) : Weather? {
        val jsonResult = JsonParser().parse(body).asJsonObject
        val channel = jsonResult
                .getAsJsonObject("query")
                .getAsJsonObject("results")
                .getAsJsonObject("channel")
        val item = channel.getAsJsonObject("item")
        val forecastArray = item.getAsJsonArray("forecast")
        val gson = GsonFactory.getGson()
        val forcastType = object : TypeToken<List<Forecast>>() {}.type
        val forecasts = gson.fromJson<List<Forecast>>(forecastArray, forcastType)
        if (forecasts.isEmpty())
            return null

        val weather = Weather()
        weather.setForecasts(forecasts)
        weather.url = item.get("link").asString
        return weather
    }
}
