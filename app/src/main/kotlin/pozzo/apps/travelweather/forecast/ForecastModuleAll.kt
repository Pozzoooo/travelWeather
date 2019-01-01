package pozzo.apps.travelweather.forecast

import pozzo.apps.travelweather.forecast.darksky.ForecastModuleDarkSky
import pozzo.apps.travelweather.forecast.yahoo.ForecastModuleYahoo
import retrofit2.Retrofit

class ForecastModuleAll : ForecastModule() {

    override fun forecastClients(retrofitBuilder: Retrofit.Builder) : List<@JvmSuppressWildcards ForecastClient> {
        val yahoo = ForecastModuleYahoo()
        val darkSky = ForecastModuleDarkSky()

        return listOf(
                darkSky.forecastClient(retrofitBuilder),
                yahoo.forecastClient(retrofitBuilder)
        )
    }
}
