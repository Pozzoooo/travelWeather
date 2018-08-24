package pozzo.apps.travelweather.map

import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.core.LastRunRepository
import pozzo.apps.travelweather.direction.DirectionWeatherFilter
import pozzo.apps.travelweather.forecast.ForecastBusiness
import pozzo.apps.travelweather.map.overlay.MapTutorial
import pozzo.apps.travelweather.map.overlay.MapTutorialScript
import pozzo.apps.travelweather.map.parser.MapPointCreator
import pozzo.apps.travelweather.map.parser.WeatherToMapPointParser

@Module
open class MapModule {
    @Provides open fun weatherToMapPointParser() = WeatherToMapPointParser()
    @Provides open fun mapTutorial() = MapTutorial()
    @Provides open fun mapTutorialScript(lastRunRepository: LastRunRepository) = MapTutorialScript(lastRunRepository)

    @Provides open fun mapPointCreator(forecastBusiness: ForecastBusiness,
                                  directionWeatherFilter: DirectionWeatherFilter,
                                  weatherToMapPointParser: WeatherToMapPointParser) =
            MapPointCreator(forecastBusiness, directionWeatherFilter, weatherToMapPointParser)
}
