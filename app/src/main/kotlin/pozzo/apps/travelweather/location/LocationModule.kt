package pozzo.apps.travelweather.location

import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.core.PermissionChecker
import pozzo.apps.travelweather.direction.DirectionLineBusiness
import pozzo.apps.travelweather.location.helper.GMapV2Direction
import pozzo.apps.travelweather.location.helper.GeoCoderBusiness

@Module
open class LocationModule {
    @Provides open fun locationBusiness(directionParser: GMapV2Direction) = LocationBusiness(directionParser)
    @Provides open fun directionLineBusiness() = DirectionLineBusiness()
    @Provides open fun directionParser() = GMapV2Direction()
    @Provides open fun locationLiveData(locationManager: LocationManager?) = LocationLiveData(locationManager)
    @Provides open fun geoCoderBusiness(application: Application) = GeoCoderBusiness(Geocoder(application))

    @Provides open fun locationManager(application: Application) =
            application.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

    @Provides open fun currentLocationRequester(permissionChecker: PermissionChecker,
                                           locationBusiness: LocationBusiness,
                                           locationManager: LocationManager?,
                                           locationLiveData: LocationLiveData) =
            CurrentLocationRequester(permissionChecker, locationBusiness, locationManager, locationLiveData)
}
