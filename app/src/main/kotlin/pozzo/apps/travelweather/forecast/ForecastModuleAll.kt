package pozzo.apps.travelweather.forecast

import pozzo.apps.travelweather.forecast.darksky.ForecastModuleDarkSky
import pozzo.apps.travelweather.forecast.weatherunlocked.ForecastModuleWeatherUnlocked
import retrofit2.Retrofit
import java.util.*
import kotlin.random.Random

class ForecastModuleAll : ForecastModule() {

    override fun forecastClients(retrofitBuilder: Retrofit.Builder) : List<@JvmSuppressWildcards ForecastClient> {
        val forecasts = TreeMap<Int, ForecastClient>(kotlin.Comparator { key1, key2 -> key1 - key2 })

        forecasts[Random.nextInt() % 1000] = ForecastModuleDarkSky().forecastClient(retrofitBuilder)
        forecasts[Random.nextInt() % 2000] = ForecastModuleWeatherUnlocked().forecastClient(retrofitBuilder)

        return forecasts.values.toList()
    }
}