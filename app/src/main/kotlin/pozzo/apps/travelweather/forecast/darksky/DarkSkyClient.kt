package pozzo.apps.travelweather.forecast.darksky

import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.forecast.ForecastClientBase
import pozzo.apps.travelweather.forecast.ForecastTypeMapper
import pozzo.apps.travelweather.forecast.model.Forecast
import pozzo.apps.travelweather.forecast.model.PoweredBy
import retrofit2.Response
import java.util.*

class DarkSkyClient(private val api: DarkSkyApi,
                    private val forecastTypeMapper: ForecastTypeMapper) :
        ForecastClientBase(PoweredBy(R.drawable.poweredbydarksky)) {

    override fun apiCall(
            coordinates: LatLng): Response<ResponseBody>? = api.forecast(coordinates.latitude,
            coordinates.longitude).execute()

    override fun handleError(response: Response<ResponseBody>?): Boolean {
        val limitExceededErrorCode = 403
        return response?.code() == limitExceededErrorCode
    }

    override fun getLinkForFullForecast(coordinates: LatLng): String {
        val language = Locale.getDefault().isO3Language
        return "https://darksky.net/forecast/${coordinates.latitude},${coordinates.longitude}/si12/$language"
    }

    override fun parseResult(body: String): List<Forecast>? {
        val jsonResult = JsonParser().parse(body).asJsonObject
        val dailyData = jsonResult.getAsJsonObject("daily").getAsJsonArray("data")

        return dailyData.map { it.asJsonObject }.map {
            val dateTime = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"))
            dateTime.timeInMillis = it.getAsJsonPrimitive("time").asLong * 1000L

            Forecast(text = it.get("summary").asString,
                    forecastType = forecastTypeMapper.getForecastType(it.get("icon").asString),
                    dateTime = dateTime,
                    high = it.get("temperatureHigh").asDouble,
                    low = it.get("temperatureLow").asDouble)
        }
    }
}
