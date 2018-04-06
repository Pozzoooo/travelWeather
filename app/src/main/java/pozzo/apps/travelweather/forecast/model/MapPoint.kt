package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng

data class MapPoint(val icon: BitmapDescriptor, val title: String?, val position: LatLng, val onClickLoadUrl: String?)
