package pozzo.apps.travelweather.location

import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import pozzo.apps.travelweather.core.PermissionChecker
import pozzo.apps.travelweather.direction.DirectionLineBusiness
import pozzo.apps.travelweather.location.google.GMapV2Direction

@Module
open class LocationModule {
    @Provides open fun locationBusiness(directionParser: GMapV2Direction) = LocationBusiness(directionParser)
    @Provides open fun directionLineBusiness() = DirectionLineBusiness()
    @Provides open fun directionParser(okHttpClient: OkHttpClient, gson: Gson) = GMapV2Direction(okHttpClient, gson)
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
