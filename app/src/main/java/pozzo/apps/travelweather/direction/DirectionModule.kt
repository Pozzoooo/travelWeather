package pozzo.apps.travelweather.direction

import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.forecast.ForecastBusiness

@Module
class DirectionModule {

    @Provides fun directionBusiness(forecastBusiness: ForecastBusiness) =
            DirectionBusiness(forecastBusiness)
}
