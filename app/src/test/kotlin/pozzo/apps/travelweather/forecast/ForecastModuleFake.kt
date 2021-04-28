package pozzo.apps.travelweather.forecast

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import pozzo.apps.travelweather.analytics.MapAnalytics
import pozzo.apps.travelweather.core.JsonParser
import pozzo.apps.travelweather.forecast.model.Weather
import retrofit2.Retrofit

class ForecastModuleFake : ForecastModule() {

    fun forecastTypeMapper(): ForecastTypeMapper {
        return object : ForecastTypeMapper {
            override fun getForecastType(type: String): ForecastType = ForecastType.THUNDERSTORMS
        }
    }

    val forecastClient by lazy { mock<ForecastClient>() }
    override fun forecastClients(retrofitBuilder: Retrofit.Builder, mapAnalytics: MapAnalytics): List<ForecastClient> {
        whenever(forecastClient.fromCoordinates(any())).thenReturn(JsonParser.fromJson(Weather::class.java, """
                    {
                      "address": {
                        "latitude": 40.1,
                        "longitude": 50.0
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
                """.trimIndent()))

        return listOf(forecastClient)
    }
}
