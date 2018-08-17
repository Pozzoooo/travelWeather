package pozzo.apps.travelweather.direction

import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.location.LocationBusiness
import pozzo.apps.travelweather.map.parser.MapPointCreator

@Module
class DirectionModule {

    @Provides fun directionBusiness(locationBusiness: LocationBusiness,
                                    directionLineBusiness: DirectionLineBusiness,
                                    mapPointCreator: MapPointCreator) =
            DirectionBusiness(locationBusiness, directionLineBusiness, mapPointCreator)

    @Provides fun directionWeatherFilter() = DirectionWeatherFilter()
}
