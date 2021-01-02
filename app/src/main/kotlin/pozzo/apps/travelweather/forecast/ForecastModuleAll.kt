package pozzo.apps.travelweather.forecast

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import pozzo.apps.travelweather.forecast.darksky.ForecastModuleDarkSky
import pozzo.apps.travelweather.forecast.openweather.ForecastModuleOpenWeather
import pozzo.apps.travelweather.forecast.weatherunlocked.ForecastModuleWeatherUnlocked
import retrofit2.Retrofit
import java.util.*
import kotlin.random.Random

@Module
@InstallIn(ApplicationComponent::class)
class ForecastModuleAll {

    @Provides fun forecastClients(retrofitBuilder: Retrofit.Builder) : List<@JvmSuppressWildcards ForecastClient> {
        val forecasts = TreeMap<Int, ForecastClient>(kotlin.Comparator { key1, key2 -> key2 - key1 })

        forecasts[Random.nextInt().and(Integer.MAX_VALUE) % 1000] = ForecastModuleDarkSky().forecastClient(retrofitBuilder)
        forecasts[Random.nextInt().and(Integer.MAX_VALUE) % 20000] = ForecastModuleWeatherUnlocked().forecastClient(retrofitBuilder)
        forecasts[Random.nextInt().and(Integer.MAX_VALUE) % 1000] = ForecastModuleOpenWeather().forecastClient(retrofitBuilder)

        return forecasts.values.toList()
    }

    @Provides fun forecastBusiness(forecastClient: List<@JvmSuppressWildcards ForecastClient>) =
            ForecastBusiness(forecastClient)
}
