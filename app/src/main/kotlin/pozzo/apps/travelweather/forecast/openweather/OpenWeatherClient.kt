package pozzo.apps.travelweather.forecast.openweather

import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.forecast.ForecastClientBase
import pozzo.apps.travelweather.forecast.ForecastTypeMapper
import pozzo.apps.travelweather.forecast.model.Forecast
import pozzo.apps.travelweather.forecast.model.PoweredBy
import retrofit2.Response
import java.net.UnknownHostException
import java.util.*

class OpenWeatherClient(private val api: OpenWeatherApi,
                        private val forecastTypeMapper: ForecastTypeMapper,
                        private val key: String) :
        ForecastClientBase(PoweredBy(R.drawable.poweredbyopenweathermap)) {

    override fun apiCall(coordinates: LatLng): Response<ResponseBody>? {
        return try {
            call(coordinates)
        } catch (e: UnknownHostException) {
            call(coordinates)//We try twice as there seems to be some dns issue related to this host
        }
    }

    private fun call(coordinates: LatLng): Response<ResponseBody>? {
        return api.forecast(coordinates.latitude,
                coordinates.longitude,
                key).execute()
    }

    override fun handleError(response: Response<ResponseBody>?): Boolean {
        val limitExceededErrorCode = 429
        val minuteLimitMaybe = 403
        return response?.code() == limitExceededErrorCode || response?.code() == minuteLimitMaybe
    }

    override fun getLinkForFullForecast(coordinates: LatLng): String {
        val language = Locale.getDefault().isO3Language
        return "https://darksky.net/forecast/${coordinates.latitude},${coordinates.longitude}/si12/$language"
//        return "https://openweathermap.org/weathermap?zoom=10&lat=${coordinates.latitude},&lon=${coordinates.longitude}"
    }

    override fun parseResult(body: String): List<Forecast>? {
        val jsonResult = JsonParser().parse(body).asJsonObject
        val dailyData = jsonResult.getAsJsonArray("list")

        return dailyData.map { it.asJsonObject }.map {
            val main = it.getAsJsonObject("main")
            val weather = it.getAsJsonArray("weather").get(0).asJsonObject
            val dateTime = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"))
            dateTime.timeInMillis = it.getAsJsonPrimitive("dt").asLong * 1000L

            Forecast(text = weather.get("description").asString,
                    forecastType = forecastTypeMapper.getForecastType(weather.get("main").asString),
                    dateTime = dateTime,
                    high = main.get("temp_max").asDouble,
                    low = main.get("temp_min").asDouble)
        }
    }
}
