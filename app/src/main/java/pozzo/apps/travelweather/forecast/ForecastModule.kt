package pozzo.apps.travelweather.forecast

import dagger.Module
import dagger.Provides

@Module
open class ForecastModule {

    @Provides open fun forecastClient() : ForecastClient = null!!
    @Provides open fun forecastTypeMapper() : ForecastTypeMapper = null!!

    @Provides fun forecastBusiness(forecastClient: ForecastClient, forecastTypeMapper: ForecastTypeMapper) =
            ForecastBusiness(forecastClient, forecastTypeMapper)
}
