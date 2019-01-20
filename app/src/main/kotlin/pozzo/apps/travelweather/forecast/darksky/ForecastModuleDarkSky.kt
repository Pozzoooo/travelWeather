package pozzo.apps.travelweather.forecast.darksky

import pozzo.apps.travelweather.BuildConfig
import pozzo.apps.travelweather.forecast.ForecastClient
import pozzo.apps.travelweather.forecast.ForecastTypeMapper
import retrofit2.Retrofit

class ForecastModuleDarkSky {

    fun forecastClient(retrofitBuilder: Retrofit.Builder): ForecastClient =
            DarkSkyClient(createApi(retrofitBuilder, baseUrl()), forecastTypeMapper())

    fun forecastTypeMapper(): ForecastTypeMapper = ForecastTypeMapperDarkSky()

    fun createApi(retrofitBuilder: Retrofit.Builder, baseUrl: String): DarkSkyApi {
        return retrofitBuilder
                .baseUrl(baseUrl)
                .build()
                .create(DarkSkyApi::class.java)
    }

    private fun baseUrl() = "https://api.darksky.net/forecast/${BuildConfig.DARK_SKY}/"
}
