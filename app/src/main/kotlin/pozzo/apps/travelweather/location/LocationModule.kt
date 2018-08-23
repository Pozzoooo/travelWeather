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
class LocationModule {
    @Provides fun locationBusiness(directionParser: GMapV2Direction) = LocationBusiness(directionParser)
    @Provides fun directionLineBusiness() = DirectionLineBusiness()
    @Provides fun directionParser() = GMapV2Direction()
    @Provides fun locationLiveData(locationManager: LocationManager?) = LocationLiveData(locationManager)
    @Provides fun geoCoderBusiness(application: Application) = GeoCoderBusiness(Geocoder(application))

    @Provides fun locationManager(application: Application) =
            application.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

    @Provides fun currentLocationRequester(permissionChecker: PermissionChecker,
                                           locationBusiness: LocationBusiness,
                                           locationManager: LocationManager?,
                                           locationLiveData: LocationLiveData) =
            CurrentLocationRequester(permissionChecker, locationBusiness, locationManager, locationLiveData)
}
