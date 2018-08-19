package pozzo.apps.travelweather.location

import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.direction.DirectionLineBusiness
import pozzo.apps.travelweather.location.helper.GMapV2Direction

@Module
class LocationModule {
    @Provides fun locationBusiness(directionParser: GMapV2Direction) = LocationBusiness(directionParser)
    @Provides fun directionLineBusiness() = DirectionLineBusiness()
    @Provides fun directionParser() = GMapV2Direction()
}
