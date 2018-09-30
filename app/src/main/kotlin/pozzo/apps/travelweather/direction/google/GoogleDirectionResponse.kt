package pozzo.apps.travelweather.direction.google

data class GoogleDirectionResponse(val routes: List<GoogleRoutes>)

data class GoogleRoutes(val legs: List<GoogleLegs>)

data class GoogleLegs(val steps: List<GoogleSteps>)

data class GoogleSteps(val start_location: GoogleLocation, val end_location: GoogleLocation, val polyline: GooglePolyline)

data class GoogleLocation(val lat: Double, val lng : Double)

data class GooglePolyline(val points: String)
