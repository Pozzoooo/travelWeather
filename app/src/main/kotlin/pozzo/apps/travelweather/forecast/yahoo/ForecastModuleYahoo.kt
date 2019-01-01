package pozzo.apps.travelweather.forecast.yahoo

import pozzo.apps.travelweather.forecast.ForecastClient
import pozzo.apps.travelweather.forecast.ForecastTypeMapper
import retrofit2.Retrofit

class ForecastModuleYahoo {

    fun forecastClient(retrofitBuilder: Retrofit.Builder): ForecastClient =
            ForecastClientYahoo(yahooWeather(retrofitBuilder, yahooBaseUrl()), forecastTypeMapper())

    private fun forecastTypeMapper(): ForecastTypeMapper = ForecastTypeMapperYahoo()

    fun yahooWeather(retrofitBuilder: Retrofit.Builder, baseUrl: String): YahooWeather {
        return retrofitBuilder
                .baseUrl(baseUrl)
                .build()
                .create(YahooWeather::class.java)
    }

    fun yahooBaseUrl() = "https://query.yahooapis.com"
}
