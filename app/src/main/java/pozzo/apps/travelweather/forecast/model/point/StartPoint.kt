package pozzo.apps.travelweather.forecast.model.point

import android.content.res.Resources
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.R

class StartPoint(res: Resources, position: LatLng) : MapPoint(
        BitmapDescriptorFactory.fromResource(R.drawable.start_flag),
        res.getString(R.string.startPosition), position, null,true, false)
