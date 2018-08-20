package pozzo.apps.travelweather.location

import android.Manifest
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.core.PermissionChecker
import pozzo.apps.travelweather.core.bugtracker.Bug

class CurrentLocationRequester(private val permissionChecker: PermissionChecker,
                               private val locationBusiness: LocationBusiness,
                               private val locationManager: LocationManager?,
                               private val locationLiveData: LocationLiveData) {
    interface Callback {
        fun onCurrentLocation(latLng: LatLng)
        fun onNotFound()
    }

    private var locationObserver: Observer<Location>? = null
    var callback: Callback? = null

    @Throws(PermissionDeniedException::class)
    fun requestCurrentLocationRequestingPermission(lifecycleOwner: LifecycleOwner) {
        if (permissionChecker.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            requestCurrentLocation(lifecycleOwner)
        } else {
            throw PermissionDeniedException()
        }
    }

    private fun requestCurrentLocation(lifecycleOwner: LifecycleOwner) {
        val currentLocation = getCurrentKnownLocation()
        if (currentLocation != null) {
            callback?.onCurrentLocation(currentLocation)
        } else {
            updateCurrentLocation(lifecycleOwner)
        }
    }

    private fun getCurrentKnownLocation(): LatLng? {
        try {
            val location = locationBusiness.getCurrentKnownLocation(locationManager)
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
                callback?.onCurrentLocation(LatLng(location.latitude, location.longitude))
            else
                callback?.onNotFound()
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
