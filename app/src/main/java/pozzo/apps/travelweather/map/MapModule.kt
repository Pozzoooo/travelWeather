package pozzo.apps.travelweather.map

import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.map.parser.WeatherToMapPointParser

@Module
class MapModule {

    @Provides fun weatherToMapPointParser() = WeatherToMapPointParser()
}