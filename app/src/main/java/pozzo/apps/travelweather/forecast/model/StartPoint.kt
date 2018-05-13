package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.R

class StartPoint(icon: BitmapDescriptor, title: String?, position: LatLng, onClickLoadUrl: String?) : MapPoint(icon, title, position, onClickLoadUrl) {

    override val icon: BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.finish_flag)
}
