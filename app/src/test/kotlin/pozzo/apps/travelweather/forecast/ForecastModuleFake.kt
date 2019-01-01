package pozzo.apps.travelweather.forecast

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.core.JsonParser
import pozzo.apps.travelweather.forecast.model.Weather
import retrofit2.Retrofit

class ForecastModuleFake : ForecastModule() {

    fun forecastTypeMapper(): ForecastTypeMapper {
        return object : ForecastTypeMapper {
            override fun getForecastType(type: String): ForecastType = ForecastType.THUNDERSTORMS
        }
    }

    override fun forecastClients(retrofitBuilder: Retrofit.Builder): List<ForecastClient> {
        return listOf(object : ForecastClient {
            override fun fromCoordinates(coordinates: LatLng): Weather? {
                return JsonParser.fromJson(Weather::class.java, """
                    {
                      "address": {
                        "latitude": ${coordinates.latitude},
                        "longitude": ${coordinates.longitude}
                      },
                      "forecasts": [
                        {
                          "date": "03 Jul 2018",
                          "text": "Thunderstorms",
                          "forecastType": "THUNDERSTORMS",
                          "high": 34,
                          "low": 22
                        }
                      ],
                      "url": "https://weather.yahoo.com/country/state/city-91558663/"
                    }
                """.trimIndent())
            }
        })
    }
}
