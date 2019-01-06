package pozzo.apps.travelweather.location

import android.annotation.SuppressLint
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import pozzo.apps.travelweather.core.bugtracker.Bug
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class LocationLiveData constructor(private val locationManager: LocationManager?) : LiveData<Location>() {
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
        try {
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0F, listener)
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0F, listener)
        } catch (e: IllegalArgumentException) {
            Bug.get().logException(e)//Emulator having no constant defined
        }
    }

    override fun onInactive() {
        locationManager?.removeUpdates(listener)
    }

    fun observeWithTimeout(owner: LifecycleOwner, observer: Observer<Location>, timeoutMillis: Long) {
        val schedule = timeoutExecutor.schedule({
            postValue(null)
        }, timeoutMillis, TimeUnit.MILLISECONDS)
        timeoutScheduleByObserver[observer] = schedule
        super.observe(owner, observer)
    }

    override fun removeObserver(observer: Observer<in Location>) {
        super.removeObserver(observer)
        timeoutScheduleByObserver[observer]?.cancel(false)
    }
}
