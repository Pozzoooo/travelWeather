package pozzo.apps.travelweather.forecast

import dagger.Module
import dagger.Provides

@Module
class ForecastModule {

    @Provides
    fun forecastClient() : ForecastClient {
        return ForecastClientFactory.instance.getForecastClient()
    }

    @Provides
    fun forecastBusiness(forecastClient: ForecastClient) : ForecastBusiness {
        return ForecastBusiness(forecastClient)
    }
}
