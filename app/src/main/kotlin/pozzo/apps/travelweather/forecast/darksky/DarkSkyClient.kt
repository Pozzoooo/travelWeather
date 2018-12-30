package pozzo.apps.travelweather.forecast.darksky

import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.forecast.ForecastClient
import pozzo.apps.travelweather.forecast.ForecastTypeMapper
import pozzo.apps.travelweather.forecast.model.Forecast
import pozzo.apps.travelweather.forecast.model.Weather
import pozzo.apps.travelweather.map.model.Address
import java.util.*

class DarkSkyClient(private val api: DarkSkyApi, private val forecastTypeMapper: ForecastTypeMapper) : ForecastClient {
    //todo needs refactoring
    override fun fromCoordinates(coordinates: LatLng): Weather? {
        val response = try {
            api.forecast(coordinates.latitude, coordinates.longitude).execute()
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
                Address(coordinates)
        )
    }

    private fun handleResponseBody(body: String?) : List<Forecast>? {
        try {
            val jsonResult = JsonParser().parse(body).asJsonObject
            val dailyData = jsonResult.getAsJsonObject("daily").getAsJsonArray("data")

            return dailyData.map { it.asJsonObject }.map {
                Forecast(text = it.get("summary").asString,
                        forecastType = forecastTypeMapper.getForecastType(it.get("icon").asString),
                        high = it.get("temperatureHigh").asDouble,
                        low = it.get("temperatureLow").asDouble
                )
            }
        } catch (e: JsonParseException) {
            Bug.get().logException(Exception("Unexpected body format: $body", e))
            return null
        }
    }
}
