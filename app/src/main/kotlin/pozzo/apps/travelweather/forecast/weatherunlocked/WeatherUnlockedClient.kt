package pozzo.apps.travelweather.forecast.weatherunlocked

import android.system.ErrnoException
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.analytics.MapAnalytics
import pozzo.apps.travelweather.forecast.ForecastClientBase
import pozzo.apps.travelweather.forecast.ForecastTypeMapper
import pozzo.apps.travelweather.forecast.model.Forecast
import pozzo.apps.travelweather.forecast.model.PoweredBy
import retrofit2.Response
import java.io.EOFException
import java.io.IOException
import java.net.SocketException
import java.util.*

class WeatherUnlockedClient(private val api: WeatherUnlockedApi, private val appId: String,
                            private val appKey: String,
                            private val typeMapper: ForecastTypeMapper,
                            private val mapAnalytics: MapAnalytics) :
        ForecastClientBase(PoweredBy(R.drawable.poweredbyweatherunlocked)) {

    override fun apiCall(coordinates: LatLng): Response<ResponseBody>? {
        return try {
            api.forecast(coordinates.latitude,
                    coordinates.longitude,
                    appId,
                    appKey).execute()
        } catch (e: ErrnoException) {
            mapAnalytics.sendKnownException("WU MissingSSL", e.toString())
            null
        } catch (e: SocketException) {
            mapAnalytics.sendKnownException("WU Timeout", e.toString())
            null
        } catch (e: EOFException) {
            mapAnalytics.sendKnownException("WU End of file", e.toString())
            null
        } catch (e: IOException) {
            mapAnalytics.sendKnownException("WU IO", e.toString())
            null
        }
    }

    override fun handleError(response: Response<ResponseBody>?): Boolean {
        val limitExceededErrorCode = 403
        return response?.code() == limitExceededErrorCode
    }

    override fun getLinkForFullForecast(coordinates: LatLng): String {
        val language = Locale.getDefault().isO3Language
        return "https://darksky.net/forecast/${coordinates.latitude},${coordinates.longitude}/si12/$language"
    }

    override fun parseResult(body: String): List<Forecast> {
        val jsonResult = JsonParser().parse(body).asJsonObject
        val dailyData = jsonResult.getAsJsonArray("Days")
        val dateTimeParser = DateTimeParserWeatherUnlocked()

        return dailyData.flatMap {
            it.asJsonObject.getAsJsonArray("Timeframes")
        }.map {
            it.asJsonObject
        }.map {
            Forecast(text = it.get("wx_desc").asString,
                    forecastType = typeMapper.getForecastType(it.get("wx_icon").asString),
                    dateTime = dateTimeParser.parse(it),
                    high = it.get("temp_f").asDouble,
                    low = it.get("temp_f").asDouble)
        }
    }
}
