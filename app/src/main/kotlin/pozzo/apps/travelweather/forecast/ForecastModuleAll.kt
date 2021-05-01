package pozzo.apps.travelweather.forecast

import pozzo.apps.travelweather.analytics.MapAnalytics
import pozzo.apps.travelweather.forecast.darksky.ForecastModuleDarkSky
import pozzo.apps.travelweather.forecast.openweather.ForecastModuleOpenWeather
import pozzo.apps.travelweather.forecast.weatherunlocked.ForecastModuleWeatherUnlocked
import retrofit2.Retrofit
import java.util.*
import kotlin.random.Random

class ForecastModuleAll : ForecastModule() {

    override fun forecastClients(retrofitBuilder: Retrofit.Builder, mapAnalytics: MapAnalytics):
            List<@JvmSuppressWildcards ForecastClient> {
        val forecasts = TreeMap<Int, ForecastClient> { key1, key2 -> key2 - key1 }

        forecasts[Random.nextInt().and(Integer.MAX_VALUE) % 5] = ForecastModuleDarkSky().forecastClient(retrofitBuilder)
        forecasts[Random.nextInt().and(Integer.MAX_VALUE) % 100] = ForecastModuleWeatherUnlocked().forecastClient(retrofitBuilder, mapAnalytics)
        forecasts[Random.nextInt().and(Integer.MAX_VALUE) % 60] = ForecastModuleOpenWeather().forecastClient(retrofitBuilder)

        return forecasts.values.toList()
    }
}
