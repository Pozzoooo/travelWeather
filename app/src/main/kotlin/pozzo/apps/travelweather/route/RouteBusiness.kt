package pozzo.apps.travelweather.route

import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.forecast.model.point.FinishPoint
import pozzo.apps.travelweather.forecast.model.point.StartPoint

/**
 * My idea here is to enable a proxy so I can have a better control on whats going on.
 */
interface RouteBusiness {
    fun createRoute(route: Route): Route
}
