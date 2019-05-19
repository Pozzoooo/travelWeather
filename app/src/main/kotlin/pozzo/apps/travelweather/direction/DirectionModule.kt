package pozzo.apps.travelweather.direction

import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.analytics.MapAnalytics
import pozzo.apps.travelweather.direction.google.GoogleDirection
import pozzo.apps.travelweather.map.parser.MapPointCreator
import pozzo.apps.travelweather.route.RouteBusiness
import pozzo.apps.travelweather.route.UnlimitedRouteBusiness

@Module
open class DirectionModule {
    @Provides open fun routeBusiness(googleDirection: GoogleDirection,
                           directionLineBusiness: DirectionLineBusiness,
                           mapPointCreator: MapPointCreator) : RouteBusiness =
            UnlimitedRouteBusiness(directionLineBusiness, mapPointCreator, googleDirection)

    @Provides open fun directionWeatherFilter(mapAnalytics: MapAnalytics) = DirectionWeatherFilter(mapAnalytics)
}
