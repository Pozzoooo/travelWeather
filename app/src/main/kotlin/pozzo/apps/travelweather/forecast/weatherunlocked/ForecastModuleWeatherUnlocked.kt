package pozzo.apps.travelweather.forecast.weatherunlocked

import pozzo.apps.travelweather.BuildConfig
import pozzo.apps.travelweather.forecast.ForecastClient
import pozzo.apps.travelweather.forecast.ForecastTypeMapper
import retrofit2.Retrofit

//todo do I really need to expose the mapper and the create api? Is there an alternative solution?
class ForecastModuleWeatherUnlocked {

    fun forecastClient(retrofitBuilder: Retrofit.Builder): ForecastClient =
            WeatherUnlockedClient(createApi(retrofitBuilder, baseUrl()), "9e2ec5bf",
                    BuildConfig.WEATHER_UNLOCKED, forecastTypeMapper())

    fun forecastTypeMapper(): ForecastTypeMapper = ForecastTypeMapperWeatherUnlocked()

    fun createApi(retrofitBuilder: Retrofit.Builder, baseUrl: String): WeatherUnlockedApi {
        return retrofitBuilder
                .baseUrl(baseUrl)
                .build()
                .create(WeatherUnlockedApi::class.java)
    }

    private fun baseUrl() = "http://api.weatherunlocked.com/api/"
}
