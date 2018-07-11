package pozzo.apps.travelweather.forecast.model.point

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import pozzo.apps.travelweather.forecast.model.Day

//todo is there a way to remove the nullability on all those fields?
abstract class MapPoint(open val icon: BitmapDescriptor?,
                        open val title: Int?,
                        open val position: LatLng,
                        open val redirectUrl: String?,
                        val isDraggable: Boolean = false,
                        val shouldFadeIn: Boolean = true,
                        var day: Day = Day.TODAY,
                        var marker: Marker? = null)
