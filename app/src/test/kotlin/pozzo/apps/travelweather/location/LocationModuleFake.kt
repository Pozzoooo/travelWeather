package pozzo.apps.travelweather.location

import android.app.Application
import android.location.LocationManager
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.mockito.Mockito
import pozzo.apps.travelweather.core.PermissionChecker
import pozzo.apps.travelweather.direction.DirectionLineBusiness
import pozzo.apps.travelweather.location.google.GoogleDirection
import pozzo.apps.travelweather.location.google.GoogleDirectionRequester
import pozzo.apps.travelweather.location.google.GoogleResponseParser
import pozzo.apps.travelweather.location.google.PolylineDecoder

class LocationModuleFake : LocationModule() {
    override fun locationBusiness(directionParser: GoogleDirection) = Mockito.mock(LocationBusiness::class.java)!!

    override fun directionLineBusiness() = Mockito.mock(DirectionLineBusiness::class.java)!!

    override fun directionParser(requester: GoogleDirectionRequester, parser: GoogleResponseParser, decoder: PolylineDecoder) =
            Mockito.mock(GoogleDirection::class.java)!!

    override fun locationLiveData(locationManager: LocationManager?) = Mockito.mock(LocationLiveData::class.java)!!

    val geoCoderBusiness by lazy { Mockito.mock(GeoCoderBusiness::class.java)!! }
    override fun geoCoderBusiness(application: Application) = geoCoderBusiness

    override fun locationManager(application: Application) = Mockito.mock(LocationManager::class.java)

    val currentLocationRequester by lazy { Mockito.mock(CurrentLocationRequester::class.java)!! }
    override fun currentLocationRequester(permissionChecker: PermissionChecker, locationBusiness: LocationBusiness,
                                          locationManager: LocationManager?, locationLiveData: LocationLiveData) = currentLocationRequester

    override fun polylineDecoder() = Mockito.mock(PolylineDecoder::class.java)!!
    override fun googleResponseParser(gson: Gson) = Mockito.mock(GoogleResponseParser::class.java)!!
    override fun googleDirectionRequester(okHttpClient: OkHttpClient) = Mockito.mock(GoogleDirectionRequester::class.java)!!
}
