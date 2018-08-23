package pozzo.apps.travelweather.map

import android.app.Application
import org.mockito.Mockito
import pozzo.apps.travelweather.direction.DirectionWeatherFilter
import pozzo.apps.travelweather.forecast.ForecastBusiness
import pozzo.apps.travelweather.map.overlay.MapTutorial
import pozzo.apps.travelweather.map.parser.MapPointCreator
import pozzo.apps.travelweather.map.parser.WeatherToMapPointParser

class MapModuleFake : MapModule() {
    override fun weatherToMapPointParser() = Mockito.mock(WeatherToMapPointParser::class.java)!!
    override fun mapTutorial(application: Application) = Mockito.mock(MapTutorial::class.java)!!
    override fun mapPointCreator(forecastBusiness: ForecastBusiness, directionWeatherFilter: DirectionWeatherFilter, weatherToMapPointParser: WeatherToMapPointParser) =
            Mockito.mock(MapPointCreator::class.java)!!
}
