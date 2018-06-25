package pozzo.apps.travelweather.forecast

import com.google.android.gms.maps.model.LatLng
import com.splunk.mint.Mint
import pozzo.apps.travelweather.forecast.model.Weather
import pozzo.apps.travelweather.map.model.Address
import java.io.IOException

class ForecastBusiness {
    companion object {
        const val MAX_RETRIES = 3
    }

    private val forecastClient = ForecastClientFactory.instance.getForecastClient()

    fun from(weatherPoints: List<LatLng>) : List<Weather> {
        var ioException: IOException? = null
        val weathers = weatherPoints.mapNotNull {
            try {
                from(it)
            } catch (e: IOException) {
                ioException = e
                null
            } catch (e: Exception) {
                Mint.logException(e)
                null
            }
        }

        if (weathers.isEmpty()) ioException?.let { throw it }
        return weathers
    }

    fun from(location: LatLng): Weather? {
        return forecastClient.fromCoordinates(location)
    }

    fun from(address: Address): Weather? {
        var i = 0
        var addressStr: String = address.address ?: return null
        do {
            try {
                if (!hasMinimalDefinition(addressStr)) return null

                val weather = forecastClient.fromAddress(addressStr)
                weather!!.address = address
                return weather
            } catch (e: Exception) {
                //ignored to retry
            }

            addressStr = reduceAddressPrecision(addressStr)
        } while (++i < MAX_RETRIES)
        return null
    }

    private fun hasMinimalDefinition(addressString: String) : Boolean = addressString.contains(",")

    private fun reduceAddressPrecision(addressString: String) : String {
        val firstCommaIdx = addressString.indexOf(",")
        return if (firstCommaIdx == -1)
            ""
        else
            addressString.substring(firstCommaIdx + 1).trim()
    }
}
