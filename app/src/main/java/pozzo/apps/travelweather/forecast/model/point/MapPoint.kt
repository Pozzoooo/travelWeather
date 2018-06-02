package pozzo.apps.travelweather.forecast.model.point

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.forecast.model.Day

abstract class MapPoint(open val icon: BitmapDescriptor?,
                        open val title: String?,
                        open val position: LatLng,
                        open val redirectUrl: String?,
                        val isDraggable: Boolean = false,
                        val shouldFadeIn: Boolean = true,
                        var day: Day = Day.TODAY)
