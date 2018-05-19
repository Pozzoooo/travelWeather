package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.R

//todo I need to find a way to show both icons (the start position icon and the weather icon)
class StartPoint(title: String?, position: LatLng, onClickLoadUrl: String?) : MapPoint(null, title, position, onClickLoadUrl, true) {

    //todo I need a proper start position icon
    override val icon: BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.finish_flag)
}
