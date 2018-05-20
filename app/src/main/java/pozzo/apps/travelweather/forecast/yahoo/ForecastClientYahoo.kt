package pozzo.apps.travelweather.forecast.yahoo

import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.splunk.mint.Mint
import okhttp3.ResponseBody
import pozzo.apps.travelweather.GsonFactory
import pozzo.apps.travelweather.forecast.ForecastBusiness
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
class ForecastClientYahoo : ForecastClient {

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

    override fun fromCoordinates(coordinates: LatLng): Weather {
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
        //todo ta meio estranho isso, pq eu to checando logo em cima e forcando aqui??
        return weather!!
    }

    private fun requestWeather(query: String) : Weather? {
        return requestWeather(query, ForecastBusiness.MAX_RETRIES)
    }

    private fun requestWeather(query: String, maxRetries: Int): Weather? {
        val response: Response<ResponseBody>
        try {
            response = ApiFactory.instance.yahooWeather.forecast(query).execute()
        } catch (e: RuntimeException) {
            e.printStackTrace()
            Mint.logException("query", query, e)
            throw e
        }

        val result = response.body()?.string()
        try {
            return handleResponse(result)
        } catch (e: ClassCastException) {
            if (maxRetries > 1)
                return requestWeather(query, maxRetries-1)
            Mint.logEvent("Json null")
            throw e
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
