package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.R

//todo new icon for the start point
class StartPoint(title: String?, position: LatLng, onClickLoadUrl: String?) :
        MapPoint(BitmapDescriptorFactory.fromResource(R.drawable.finish_flag), title, position, onClickLoadUrl, true, false)
