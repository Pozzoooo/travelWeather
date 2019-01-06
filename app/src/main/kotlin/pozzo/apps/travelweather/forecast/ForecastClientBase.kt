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

abstract class ForecastClientBase(private val poweredBy: PoweredBy) : ForecastClient {
    abstract fun apiCall(coordinates: LatLng) : Response<ResponseBody>?
    abstract fun handleError(response: Response<ResponseBody>?): Boolean
    abstract fun parseResult(body: String) : List<Forecast>?
    abstract fun getLinkForFullForecast(coordinates: LatLng): String

    override fun fromCoordinates(coordinates: LatLng): Weather? {
        val response = makeRequest(coordinates)
        val result = validateResponse(response)

        if (result != null) {
            val forecasts = handleSuccessResponseBody(result) ?: return null

            return Weather(getLinkForFullForecast(coordinates),
                    forecasts, Address(coordinates), poweredBy)
        }
        return null
    }

    private fun makeRequest(coordinates: LatLng) : Response<ResponseBody>? {
        return try {
            apiCall(coordinates)
        } catch (e: Exception) {
            Bug.get().logException(e)
            return null
        }
    }

    private fun validateResponse(response: Response<ResponseBody>?): String? {
        val result = response?.body()?.string()
        return if (result?.isEmpty() != false || !response.isSuccessful) {
            if(!handleError(response)) {
                Bug.get().logException(Exception("Null body, code: ${response?.code()}, error: ${response?.errorBody()?.string()}"))
            }
            null
        } else {
            result
        }
    }

    private fun handleSuccessResponseBody(body: String) : List<Forecast>? {
        return try {
            parseResult(body)
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
