package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.R

class FinishPoint(title: String?, position: LatLng, onClickLoadUrl: String?) : MapPoint(null, title, position, onClickLoadUrl, true) {

    //todo is there a better way to do it?
    override val icon: BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.finish_flag)
}
