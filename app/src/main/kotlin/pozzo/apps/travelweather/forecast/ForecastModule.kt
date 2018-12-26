package pozzo.apps.travelweather.forecast

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
open class ForecastModule {

    @Provides open fun forecastClient(retrofitBuilder: Retrofit.Builder, forecastTypeMapper: ForecastTypeMapper) : ForecastClient = null!!
    @Provides open fun forecastTypeMapper() : ForecastTypeMapper = null!!

    @Provides fun forecastBusiness(forecastClient: ForecastClient) =
            ForecastBusiness(forecastClient)
}
