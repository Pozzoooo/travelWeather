package pozzo.apps.travelweather.map.model

import com.google.android.gms.maps.model.LatLng

/**
 * Represent a single point in map.
 */
data class Address(val latLng: LatLng,
                   var address: String? = null)
