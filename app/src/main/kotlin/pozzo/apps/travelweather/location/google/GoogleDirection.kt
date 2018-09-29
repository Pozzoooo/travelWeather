package pozzo.apps.travelweather.location.google

import com.google.android.gms.maps.model.LatLng

//todo seems like location and direction packages are a bit mixed, need to fix it
class GoogleDirection(private val requester: GoogleDirectionRequester,
                      private val parser: GoogleResponseParser,
                      private val polylineDecoder: PolylineDecoder) {

    fun getDirection(start: LatLng, end: LatLng) : List<LatLng>? {
        val response = requester.request(start, end)
        val parsedResponse = response?.let { parser.parse(response) }
        //todo need to understand what do I need to loop
        return parsedResponse?.let { polylineDecoder.decode(parsedResponse.routes[0].legs[0].steps[0].polyline.points) }
    }
}
