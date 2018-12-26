package pozzo.apps.travelweather.forecast.darksky

import pozzo.apps.travelweather.BuildConfig
import pozzo.apps.travelweather.forecast.ForecastClient
import pozzo.apps.travelweather.forecast.ForecastModule
import pozzo.apps.travelweather.forecast.ForecastTypeMapper
import retrofit2.Retrofit

class ForecastModuleDarkSky : ForecastModule() {

    override fun forecastClient(retrofitBuilder: Retrofit.Builder, forecastTypeMapper: ForecastTypeMapper): ForecastClient =
            DarkSkyClient(createApi(retrofitBuilder, baseUrl()), forecastTypeMapper)

    override fun forecastTypeMapper(): ForecastTypeMapper = ForecastTypeMapperDarkSky()

    private fun createApi(retrofitBuilder: Retrofit.Builder, baseUrl: String): DarkSkyApi {
        return retrofitBuilder
                .baseUrl(baseUrl)
                .build()
                .create(DarkSkyApi::class.java)
    }

    private fun baseUrl() = "https://api.darksky.net/forecast/${BuildConfig.DARK_SKY}/"
}
//TODO preciso adicionar ao analytics o numero de requisicoes a previsao do tempo q eu fiz
