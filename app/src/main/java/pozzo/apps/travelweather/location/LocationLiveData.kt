package pozzo.apps.travelweather.location

import android.arch.lifecycle.LiveData
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.annotation.MainThread

class LocationLiveData private constructor(context: Context) : LiveData<Location>() {
    private val locationManager: LocationManager

    private val listener = object : LocationListener {
        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) { }
        override fun onProviderEnabled(p0: String?) { }
        override fun onProviderDisabled(p0: String?) { }

        override fun onLocationChanged(location: Location) {
            value = location
        }
    }

    init {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onActive() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0F, listener)
    }

    override fun onInactive() {
        locationManager.removeUpdates(listener)
    }

    companion object {
        private var instance: LocationLiveData? = null

        @MainThread
        operator fun get(context: Context): LocationLiveData {
            if (instance == null) {
                instance = LocationLiveData(context.applicationContext)
            }
            return instance as LocationLiveData
        }
    }
}
