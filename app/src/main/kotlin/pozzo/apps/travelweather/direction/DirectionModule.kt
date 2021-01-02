package pozzo.apps.travelweather.direction

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import pozzo.apps.travelweather.common.business.PreferencesBusiness
import pozzo.apps.travelweather.direction.google.GoogleDirection
import pozzo.apps.travelweather.map.parser.MapPointCreator
import pozzo.apps.travelweather.route.RouteBusiness
import pozzo.apps.travelweather.route.UnlimitedRouteBusiness

@Module
@InstallIn(ApplicationComponent::class)
open class DirectionModule {
    @Provides open fun routeBusiness(googleDirection: GoogleDirection,
                           directionLineBusiness: DirectionLineBusiness,
                           mapPointCreator: MapPointCreator,
                           preferencesBusiness: PreferencesBusiness,
                           directionWeatherFilter: DirectionWeatherFilter): RouteBusiness =
            UnlimitedRouteBusiness(directionLineBusiness, mapPointCreator, googleDirection, directionWeatherFilter)
}
