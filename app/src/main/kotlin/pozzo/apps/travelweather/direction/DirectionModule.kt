package pozzo.apps.travelweather.direction

import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.direction.google.GoogleDirection
import pozzo.apps.travelweather.map.parser.MapPointCreator

@Module
open class DirectionModule {
    @Provides open fun directionBusiness(googleDirection: GoogleDirection,
                                         directionLineBusiness: DirectionLineBusiness,
                                         mapPointCreator: MapPointCreator) =
            DirectionBusiness(directionLineBusiness, mapPointCreator, googleDirection)

    @Provides open fun directionWeatherFilter() = DirectionWeatherFilter()
}
