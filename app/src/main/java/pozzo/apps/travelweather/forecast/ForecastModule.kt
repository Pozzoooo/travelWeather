package pozzo.apps.travelweather.forecast

import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.forecast.yahoo.ForecastClientYahoo

@Module
open class ForecastModule {

    @Provides open fun forecastClient() : ForecastClient = ForecastClientYahoo()

    @Provides fun forecastBusiness(forecastClient: ForecastClient) = ForecastBusiness(forecastClient)
}
