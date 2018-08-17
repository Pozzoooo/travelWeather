package pozzo.apps.travelweather.location

import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.direction.DirectionLineBusiness

@Module
class LocationModule {
    @Provides fun locationBusiness() = LocationBusiness()
    @Provides fun directionLineBusiness() = DirectionLineBusiness()
}
