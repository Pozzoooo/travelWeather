package pozzo.apps.travelweather.map

import android.app.Application
import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.direction.DirectionWeatherFilter
import pozzo.apps.travelweather.forecast.ForecastBusiness
import pozzo.apps.travelweather.map.overlay.MapTutorial
import pozzo.apps.travelweather.map.parser.MapPointCreator
import pozzo.apps.travelweather.map.parser.WeatherToMapPointParser

@Module
open class MapModule {
    @Provides open fun weatherToMapPointParser() = WeatherToMapPointParser()
    @Provides open fun mapTutorial(application: Application) = MapTutorial(application)

    @Provides open fun mapPointCreator(forecastBusiness: ForecastBusiness,
                                  directionWeatherFilter: DirectionWeatherFilter,
                                  weatherToMapPointParser: WeatherToMapPointParser) =
            MapPointCreator(forecastBusiness, directionWeatherFilter, weatherToMapPointParser)
}
