package pozzo.apps.travelweather.forecast.weatherunlocked

import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.forecast.ForecastClient
import pozzo.apps.travelweather.forecast.ForecastTypeMapper
import pozzo.apps.travelweather.forecast.model.Forecast
import pozzo.apps.travelweather.forecast.model.PoweredBy
import pozzo.apps.travelweather.forecast.model.Weather
import pozzo.apps.travelweather.map.model.Address
import java.util.*

class WeatherUnlockedClient(private val api: WeatherUnlockedApi, private val appId: String,
                            private val appKey: String, private val typeMapper: ForecastTypeMapper) : ForecastClient {
    private val poweredByWeatherUnlocked = PoweredBy(R.drawable.poweredbyweatherunlocked)

    override fun fromCoordinates(coordinates: LatLng): Weather? {
        val response = try {//todo it sounds like I can make a good reuse between the clients
            api.forecast(coordinates.latitude, coordinates.longitude, appId, appKey).execute()
        } catch (e: Exception) {
            Bug.get().logException(e)
            return null
        }

        val result = response?.body()?.string()
        if (result?.isEmpty() != false) {
            Bug.get().logException(Exception("Null body, code: ${response.code()}, error: ${response.errorBody()?.string()}"))
            return null
        }

        val forecasts = handleResponseBody(result) ?: return null
        val language = Locale.getDefault().isO3Language
        return Weather(
                "https://darksky.net/forecast/${coordinates.latitude},${coordinates.longitude}/si12/$language",
                forecasts,
                Address(coordinates),
                poweredByWeatherUnlocked
        )
    }

    private fun handleResponseBody(body: String?): List<Forecast>? {
        return try {
            val jsonResult = JsonParser().parse(body).asJsonObject
            val dailyData = jsonResult.getAsJsonArray("Days")

            //todo definitivamente preciso testar isso
            dailyData.map {
                val timeFrames = it.asJsonObject.getAsJsonArray("Timeframes")
                timeFrames.get(timeFrames.size() / 2).asJsonObject
            }.map {
                Forecast(text = it.get("wx_desc").asString,
                        forecastType = typeMapper.getForecastType(it.get("wx_icon").asString),
                        high = it.get("temp_c").asDouble,
                        low = it.get("temp_c").asDouble
                )
            }
        } catch (e: JsonParseException) {
            Bug.get().logException(Exception("Unexpected body format: $body", e))
            null
        } catch (e: IndexOutOfBoundsException) {
            Bug.get().logException(e)
            null
        } catch (e: NullPointerException) {
            Bug.get().logException(e)
            null
        }
    }
}
