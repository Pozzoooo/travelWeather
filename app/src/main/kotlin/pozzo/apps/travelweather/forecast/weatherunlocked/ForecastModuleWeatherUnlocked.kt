package pozzo.apps.travelweather.forecast.weatherunlocked

import pozzo.apps.travelweather.BuildConfig
import pozzo.apps.travelweather.analytics.MapAnalytics
import pozzo.apps.travelweather.forecast.ForecastClient
import retrofit2.Retrofit

class ForecastModuleWeatherUnlocked {

    fun forecastClient(retrofitBuilder: Retrofit.Builder, mapAnalytics: MapAnalytics): ForecastClient =
            WeatherUnlockedClient(createApi(retrofitBuilder, baseUrl()), "9e2ec5bf",
                    BuildConfig.WEATHER_UNLOCKED, ForecastTypeMapperWeatherUnlocked(), mapAnalytics)

    private fun createApi(retrofitBuilder: Retrofit.Builder, baseUrl: String): WeatherUnlockedApi {
        return retrofitBuilder
                .baseUrl(baseUrl)
                .build()
                .create(WeatherUnlockedApi::class.java)
    }

    private fun baseUrl() = "http://api.weatherunlocked.com/api/"
}
