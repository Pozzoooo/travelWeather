package pozzo.apps.travelweather.forecast

import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.analytics.MapAnalytics
import retrofit2.Retrofit

@Module
open class ForecastModule {

    @Provides open fun forecastClients(retrofitBuilder: Retrofit.Builder,
                                       mapAnalytics: MapAnalytics):
            List<@JvmSuppressWildcards ForecastClient> = null!!

    @Provides fun forecastBusiness(forecastClient: List<@JvmSuppressWildcards ForecastClient>) =
            ForecastBusiness(forecastClient)
}
