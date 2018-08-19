package pozzo.apps.travelweather.location

import android.app.Application
import android.content.Context
import android.location.LocationManager
import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.direction.DirectionLineBusiness
import pozzo.apps.travelweather.location.helper.GMapV2Direction

@Module
class LocationModule {
    @Provides fun locationBusiness(directionParser: GMapV2Direction) = LocationBusiness(directionParser)
    @Provides fun directionLineBusiness() = DirectionLineBusiness()
    @Provides fun directionParser() = GMapV2Direction()
    @Provides fun locationLiveData(locationManager: LocationManager?) = LocationLiveData(locationManager)

    @Provides fun locationManager(application: Application) =
            application.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

    @Provides fun currentLocationRequester(application: Application,
                                           locationBusiness: LocationBusiness,
                                           locationManager: LocationManager?,
                                           locationLiveData: LocationLiveData) =
            CurrentLocationRequester(application, locationBusiness, locationManager, locationLiveData)
}
