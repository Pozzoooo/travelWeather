package pozzo.apps.travelweather.forecast

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
open class ForecastModule {

    @Provides open fun forecastClients(retrofitBuilder: Retrofit.Builder) : List<@JvmSuppressWildcards ForecastClient> = null!!

    @Provides fun forecastBusiness(forecastClient: List<@JvmSuppressWildcards ForecastClient>) =
            ForecastBusiness(forecastClient)
}
