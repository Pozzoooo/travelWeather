package pozzo.apps.travelweather.forecast.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.forecast.model.point.WeatherPoint

class ForecastInfoWindowAdapter(context: Context) : GoogleMap.InfoWindowAdapter {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        val contentView = inflater.inflate(R.layout.adapter_forecast, null)
        val lTitle = contentView.findViewById<TextView>(R.id.lTitle)
        lTitle.text = marker.title

        val mapPoint = marker.tag
        if (mapPoint is WeatherPoint) {
            val poweredByImage = contentView.findViewById<ImageView>(R.id.poweredByImage)
            poweredByImage.setImageResource(mapPoint.poweredBy.icon)
        }
        return contentView
    }
}
