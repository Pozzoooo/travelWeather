package pozzo.apps.travelweather.forecast.darksky

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
import java.lang.NullPointerException
import java.util.*

class DarkSkyClient(private val api: DarkSkyApi, private val forecastTypeMapper: ForecastTypeMapper) : ForecastClient {
    private val poweredByDarkSky = PoweredBy(R.drawable.poweredbydarksky)

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
                Address(coordinates),
                poweredByDarkSky
        )
    }

    private fun handleResponseBody(body: String?) : List<Forecast>? {
        return try {
            val jsonResult = JsonParser().parse(body).asJsonObject
            val dailyData = jsonResult.getAsJsonObject("daily").getAsJsonArray("data")

            dailyData.map { it.asJsonObject }.map {
                Forecast(text = it.get("summary").asString,
                        forecastType = forecastTypeMapper.getForecastType(it.get("icon").asString),
                        high = it.get("temperatureHigh").asDouble,
                        low = it.get("temperatureLow").asDouble
                )
            }
        } catch (e: JsonParseException) {
            Bug.get().logException(Exception("Unexpected body format: $body", e))
            null
        } catch (e: NullPointerException) {
            Bug.get().logException(e)
            null
        }
    }
}
