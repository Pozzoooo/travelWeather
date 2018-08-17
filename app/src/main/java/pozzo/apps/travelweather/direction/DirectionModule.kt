package pozzo.apps.travelweather.direction

import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.forecast.ForecastBusiness
import pozzo.apps.travelweather.location.LocationBusiness
import pozzo.apps.travelweather.map.parser.WeatherToMapPointParser

@Module
class DirectionModule {

    @Provides fun directionBusiness(forecastBusiness: ForecastBusiness,
            locationBusiness: LocationBusiness,
            directionLineBusiness: DirectionLineBusiness,
            directionWeatherFilter: DirectionWeatherFilter,
            weatherToMapPointParser: WeatherToMapPointParser) : DirectionBusiness {

        return DirectionBusiness(forecastBusiness, locationBusiness,
                directionLineBusiness, directionWeatherFilter, weatherToMapPointParser)
    }

    @Provides fun directionWeatherFilter() = DirectionWeatherFilter()
}
