package pozzo.apps.travelweather.forecast

import dagger.Module
import dagger.Provides

@Module
open class ForecastModule {

    @Provides open fun forecastClient() : ForecastClient = null!!

    @Provides fun forecastBusiness(forecastClient: ForecastClient) = ForecastBusiness(forecastClient)
}
