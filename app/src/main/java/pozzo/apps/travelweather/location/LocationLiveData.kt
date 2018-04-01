package pozzo.apps.travelweather.location

import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.annotation.MainThread
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class LocationLiveData private constructor(context: Context) : LiveData<Location>() {
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

    private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val timeoutExecutor = Executors.newSingleThreadScheduledExecutor()
    private val timeoutScheduleByObserver = HashMap<Observer<*>, ScheduledFuture<*>>()

    private val listener = object : LocationListener {
        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) { }
        override fun onProviderEnabled(p0: String?) { }
        override fun onProviderDisabled(p0: String?) { }

        override fun onLocationChanged(location: Location) {
            value = location
        }
    }

    @SuppressLint("MissingPermission")
    override fun onActive() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0F, listener)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0F, listener)
    }

    override fun onInactive() {
        locationManager.removeUpdates(listener)
    }

    fun observeWithTimeout(owner: LifecycleOwner, observer: Observer<Location>, timeoutMillis: Long) {
        val schedule = timeoutExecutor.schedule({
            postValue(null)
        }, timeoutMillis, TimeUnit.MILLISECONDS)
        timeoutScheduleByObserver[observer] = schedule
        super.observe(owner, observer)
    }

    override fun removeObserver(observer: Observer<Location>) {
        super.removeObserver(observer)
        timeoutScheduleByObserver[observer]?.cancel(false)
    }
}
