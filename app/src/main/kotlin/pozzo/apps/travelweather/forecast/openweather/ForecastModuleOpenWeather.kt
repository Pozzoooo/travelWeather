package pozzo.apps.travelweather.forecast.openweather

import pozzo.apps.travelweather.BuildConfig
import pozzo.apps.travelweather.forecast.ForecastClient
import pozzo.apps.travelweather.forecast.ForecastTypeMapper
import retrofit2.Retrofit

class ForecastModuleOpenWeather {

    fun forecastClient(retrofitBuilder: Retrofit.Builder): ForecastClient =
            OpenWeatherClient(createApi(retrofitBuilder, baseUrl()), forecastTypeMapper(), BuildConfig.OPEN_WEATHER)

    fun forecastTypeMapper(): ForecastTypeMapper = ForecastTypeMapperOpenWeather()

    fun createApi(retrofitBuilder: Retrofit.Builder, baseUrl: String): OpenWeatherApi {
        return retrofitBuilder
                .baseUrl(baseUrl)
                .build()
                .create(OpenWeatherApi::class.java)
    }

    private fun baseUrl() = "https://api.openweathermap.org/"
}
