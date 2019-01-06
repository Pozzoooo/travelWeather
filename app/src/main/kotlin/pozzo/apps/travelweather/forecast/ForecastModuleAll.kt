package pozzo.apps.travelweather.forecast

import pozzo.apps.travelweather.forecast.darksky.ForecastModuleDarkSky
import pozzo.apps.travelweather.forecast.weatherunlocked.ForecastModuleWeatherUnlocked
import retrofit2.Retrofit

class ForecastModuleAll : ForecastModule() {

    override fun forecastClients(retrofitBuilder: Retrofit.Builder) : List<@JvmSuppressWildcards ForecastClient> {
        val darkSky = ForecastModuleDarkSky()
        val weatherUnlocked = ForecastModuleWeatherUnlocked()

        return listOf(
                darkSky.forecastClient(retrofitBuilder),
                weatherUnlocked.forecastClient(retrofitBuilder)
        )
    }
}
