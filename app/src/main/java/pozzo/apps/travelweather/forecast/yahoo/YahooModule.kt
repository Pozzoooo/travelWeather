package pozzo.apps.travelweather.forecast.yahoo

import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.core.injection.NetworkModule
import retrofit2.Retrofit

@Module(includes = [NetworkModule::class])
class YahooModule {

    @Provides fun yahooWeather(retrofitBuilder: Retrofit.Builder): YahooWeather {
        return retrofitBuilder
                .baseUrl("https://query.yahooapis.com")
                .build()
                .create(YahooWeather::class.java)
    }
}
