package pozzo.apps.travelweather.forecast

import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonParseException
import okhttp3.ResponseBody
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.forecast.model.Forecast
import pozzo.apps.travelweather.forecast.model.PoweredBy
import pozzo.apps.travelweather.forecast.model.Weather
import pozzo.apps.travelweather.map.model.Address
import retrofit2.Response

/**
 * todo I'm not really sure about this solution
 *  There is a big issue in here where I'm exposing all those methods which I didn't really want to
 */
abstract class ForecastClientBase(private val poweredBy: PoweredBy) : ForecastClient {
    abstract fun apiCall(coordinates: LatLng) : Response<ResponseBody>?
    abstract fun handleError(response: Response<ResponseBody>?): Boolean
    abstract fun parseResult(body: String) : List<Forecast>?
    abstract fun getLinkForFullForecast(coordinates: LatLng): String

    override fun fromCoordinates(coordinates: LatLng): Weather? {
        val response = makeRequest(coordinates) ?: return null
        val result = validateResponse(response) ?: return null
        val forecasts = handleSuccessResponseBody(result) ?: return null
        if (forecasts.isEmpty()) return null
        return Weather(getLinkForFullForecast(coordinates), forecasts, Address(coordinates), poweredBy)
    }

    private fun makeRequest(coordinates: LatLng) : Response<ResponseBody>? {
        return try {
            apiCall(coordinates)
        } catch (e: Exception) {
            Bug.get().logException(e)
            null
        }
    }

    private fun validateResponse(response: Response<ResponseBody>): String? {
        val result = response.body()?.string()
        if (result?.isEmpty() != false || !response.isSuccessful) {
            if(!handleError(response)) {
                Bug.get().logException(Exception("Null body ${this.javaClass.simpleName}, " +
                        "code: ${response.code()}, error: ${response.errorBody()?.string()}"))
            }
            return null
        }
        return result
    }

    private fun handleSuccessResponseBody(body: String) : List<Forecast>? {
        try {
            return parseResult(body)
        } catch (e: JsonParseException) {
            logException(body, e)
        } catch (e: IllegalStateException) {
            logException(body, e)
        } catch (e: IndexOutOfBoundsException) {
            logException(body, e)
        } catch (e: NullPointerException) {
            logException(body, e)
        }
        return null
    }

    private fun logException(body: String, e: Exception) {
        Bug.get().logException(Exception("Unexpected body format: $body", e))
    }
}
