package pozzo.apps.travelweather.map

import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.direction.DirectionWeatherFilter
import pozzo.apps.travelweather.forecast.ForecastBusiness
import pozzo.apps.travelweather.map.parser.MapPointCreator
import pozzo.apps.travelweather.map.parser.WeatherToMapPointParser

@Module
class MapModule {
    @Provides fun weatherToMapPointParser() = WeatherToMapPointParser()

    @Provides fun mapPointCreator(forecastBusiness: ForecastBusiness,
                                  directionWeatherFilter: DirectionWeatherFilter,
                                  weatherToMapPointParser: WeatherToMapPointParser) =
            MapPointCreator(forecastBusiness, directionWeatherFilter, weatherToMapPointParser)
}