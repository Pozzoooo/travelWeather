package pozzo.apps.travelweather.map

import org.mockito.Mockito
import pozzo.apps.travelweather.core.LastRunRepository
import pozzo.apps.travelweather.direction.DirectionWeatherFilter
import pozzo.apps.travelweather.forecast.ForecastBusiness
import pozzo.apps.travelweather.map.overlay.MapTutorial
import pozzo.apps.travelweather.map.overlay.MapTutorialScript
import pozzo.apps.travelweather.map.parser.MapPointCreator
import pozzo.apps.travelweather.map.parser.WeatherToMapPointParser

class MapModuleFake : MapModule() {
    override fun weatherToMapPointParser() = Mockito.mock(WeatherToMapPointParser::class.java)!!
    override fun mapTutorial() = Mockito.mock(MapTutorial::class.java)!!
    override fun mapTutorialScript(lastRunRepository: LastRunRepository) = Mockito.mock(MapTutorialScript::class.java)!!
    override fun mapPointCreator(forecastBusiness: ForecastBusiness, weatherToMapPointParser: WeatherToMapPointParser) =
            Mockito.mock(MapPointCreator::class.java)!!
}
