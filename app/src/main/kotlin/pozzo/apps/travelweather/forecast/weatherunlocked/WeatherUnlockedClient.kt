package pozzo.apps.travelweather.forecast.weatherunlocked

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

class WeatherUnlockedClient(private val api: WeatherUnlockedApi, private val appId: String,
                            private val appKey: String, private val typeMapper: ForecastTypeMapper) :
        ForecastClientBase(PoweredBy(R.drawable.poweredbyweatherunlocked)) {

    override fun apiCall(coordinates: LatLng): Response<ResponseBody>? =
            api.forecast(coordinates.latitude, coordinates.longitude, appId, appKey).execute()

    override fun handleError(response: Response<ResponseBody>?): Boolean = false

    override fun getLinkForFullForecast(coordinates: LatLng): String {
        val language = Locale.getDefault().isO3Language
        return "https://darksky.net/forecast/${coordinates.latitude},${coordinates.longitude}/si12/$language"
    }

    override fun parseResult(body: String): List<Forecast>? {
        val jsonResult = JsonParser().parse(body).asJsonObject
        val dailyData = jsonResult.getAsJsonArray("Days")

        //todo definitivamente preciso testar isso
        return dailyData.map {
            val timeFrames = it.asJsonObject.getAsJsonArray("Timeframes")
            timeFrames.get(timeFrames.size() / 2).asJsonObject
        }.map {
            Forecast(text = it.get("wx_desc").asString,
                    forecastType = typeMapper.getForecastType(it.get("wx_icon").asString),
                    high = it.get("temp_c").asDouble,
                    low = it.get("temp_c").asDouble
            )
        }
    }
}
