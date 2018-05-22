package pozzo.apps.travelweather.forecast.model.point

import android.content.res.Resources
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.R

class FinishPoint(res: Resources, position: LatLng) : MapPoint(
        BitmapDescriptorFactory.fromResource(R.drawable.finish_flag),
        res.getString(R.string.finishPosition), position, null,true, false)
