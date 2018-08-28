package pozzo.apps.travelweather.direction

import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.location.LocationBusiness
import pozzo.apps.travelweather.map.parser.MapPointCreator

@Module
open class DirectionModule {
    @Provides open fun directionBusiness(locationBusiness: LocationBusiness,
                                    directionLineBusiness: DirectionLineBusiness,
                                    mapPointCreator: MapPointCreator) =
            DirectionBusiness(locationBusiness, directionLineBusiness, mapPointCreator)

    @Provides open fun directionWeatherFilter() = DirectionWeatherFilter()
}
