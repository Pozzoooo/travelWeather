package pozzo.apps.travelweather.map.model

/**
 * Represent a single point in map.
 */
data class Address(val latitude: Double,
                   val longitude: Double,
                   var address: String? = null)
