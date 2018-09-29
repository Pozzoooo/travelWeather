package pozzo.apps.travelweather.location

import android.app.Application
import android.location.LocationManager
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.mockito.Mockito
import pozzo.apps.travelweather.core.PermissionChecker
import pozzo.apps.travelweather.direction.DirectionLineBusiness
import pozzo.apps.travelweather.location.google.GMapV2Direction

class LocationModuleFake : LocationModule() {
    override fun locationBusiness(directionParser: GMapV2Direction) = Mockito.mock(LocationBusiness::class.java)!!

    override fun directionLineBusiness() = Mockito.mock(DirectionLineBusiness::class.java)!!

    override fun directionParser(okHttpClient: OkHttpClient, gson: Gson) = Mockito.mock(GMapV2Direction::class.java)!!

    override fun locationLiveData(locationManager: LocationManager?) = Mockito.mock(LocationLiveData::class.java)!!

    val geoCoderBusiness by lazy { Mockito.mock(GeoCoderBusiness::class.java)!! }
    override fun geoCoderBusiness(application: Application) = geoCoderBusiness

    override fun locationManager(application: Application) = Mockito.mock(LocationManager::class.java)

    val currentLocationRequester by lazy { Mockito.mock(CurrentLocationRequester::class.java)!! }
    override fun currentLocationRequester(permissionChecker: PermissionChecker, locationBusiness: LocationBusiness,
                                          locationManager: LocationManager?, locationLiveData: LocationLiveData) = currentLocationRequester
}
