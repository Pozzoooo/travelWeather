package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng

open class MapPoint(open val icon: BitmapDescriptor?,
                    open val title: String?,
                    open val position: LatLng,
                    open val onClickLoadUrl: String?)
