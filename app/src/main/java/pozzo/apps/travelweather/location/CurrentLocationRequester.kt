package pozzo.apps.travelweather.location

import android.Manifest
import android.app.Application
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.support.v4.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.location.helper.GMapV2Direction

class CurrentLocationRequester(private val application: Application, private val callback: Callback) {
    companion object {
        interface Callback {
            fun onCurrentLocation(latLng: LatLng)
            fun onNotFound()
        }
    }

    private val locationBusiness = LocationBusiness(GMapV2Direction())

    private val locationLiveData = LocationLiveData(application)
    private var locationObserver: Observer<Location>? = null

    @Throws(PermissionDeniedException::class)
    fun requestCurrentLocationRequestingPermission(lifecycleOwner: LifecycleOwner) {
        if (hasLocationPermission()) {
            requestCurrentLocation(lifecycleOwner)
        } else {
            throw PermissionDeniedException()
        }
    }

    private fun hasLocationPermission() : Boolean = Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1
            || ContextCompat.checkSelfPermission(application, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun requestCurrentLocation(lifecycleOwner: LifecycleOwner) {
        val currentLocation = getCurrentKnownLocation()
        if (currentLocation != null) {
            callback.onCurrentLocation(currentLocation)
        } else {
            updateCurrentLocation(lifecycleOwner)
        }
    }

    private fun getCurrentKnownLocation(): LatLng? {
        try {
            val location = locationBusiness.getCurrentKnownLocation(application)
            return if (location != null) LatLng(location.latitude, location.longitude) else null
        } catch (e: SecurityException) {
            //we might not have permission, we leave the system try to activate the gps before any message
        } catch (e: Exception) {
            Bug.get().logException(e)
        }
        return null
    }

    private fun updateCurrentLocation(lifecycleOwner: LifecycleOwner) {
        val locationObserver = Observer<Location> { location ->
            removeLocationObserver()
            if (location != null)
                callback.onCurrentLocation(LatLng(location.latitude, location.longitude))
            else
                callback.onNotFound()
        }
        locationLiveData.observeWithTimeout(lifecycleOwner, locationObserver, 30000L)
        this.locationObserver = locationObserver
    }

    fun removeLocationObserver() {
        locationObserver?.let {
            locationLiveData.removeObserver(it)
        }
    }
}
