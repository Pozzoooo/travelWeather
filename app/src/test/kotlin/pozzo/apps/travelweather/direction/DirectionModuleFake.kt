package pozzo.apps.travelweather.direction

import org.mockito.Mockito
import pozzo.apps.travelweather.location.LocationBusiness
import pozzo.apps.travelweather.map.parser.MapPointCreator

class DirectionModuleFake : DirectionModule() {
    val directionBusiness by lazy { Mockito.mock(DirectionBusiness::class.java)!! }
    override fun directionBusiness(locationBusiness: LocationBusiness, directionLineBusiness: DirectionLineBusiness,
                                   mapPointCreator: MapPointCreator) = directionBusiness

    val directionWeatherFilter by lazy { Mockito.mock(DirectionWeatherFilter::class.java)!! }
    override fun directionWeatherFilter() = directionWeatherFilter
}
