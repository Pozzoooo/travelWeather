package pozzo.apps.travelweather.direction

import org.mockito.Mockito
import pozzo.apps.travelweather.analytics.MapAnalytics
import pozzo.apps.travelweather.common.business.PreferencesBusiness
import pozzo.apps.travelweather.direction.google.GoogleDirection
import pozzo.apps.travelweather.map.parser.MapPointCreator
import pozzo.apps.travelweather.route.RouteBusiness

class DirectionModuleFake : DirectionModule() {
    val directionBusiness by lazy { Mockito.mock(RouteBusiness::class.java)!! }
    override fun routeBusiness(googleDirection: GoogleDirection,
                           directionLineBusiness: DirectionLineBusiness,
                           mapPointCreator: MapPointCreator,
                           preferencesBusiness: PreferencesBusiness): RouteBusiness = directionBusiness

    val directionWeatherFilter by lazy { Mockito.mock(DirectionWeatherFilter::class.java)!! }
    override fun directionWeatherFilter(mapAnalytics: MapAnalytics) = directionWeatherFilter
}
