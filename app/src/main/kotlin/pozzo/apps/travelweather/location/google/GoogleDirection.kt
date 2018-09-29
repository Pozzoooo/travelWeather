package pozzo.apps.travelweather.location.google

import com.google.android.gms.maps.model.LatLng

//todo seems like location and direction packages are a bit mixed, need to fix it
class GoogleDirection(private val requester: GoogleDirectionRequester,
                      private val parser: GoogleResponseParser,
                      private val polylineDecoder: PolylineDecoder) {

    fun getDirection(start: LatLng, end: LatLng) : List<LatLng>? {
        val response = requester.request(start, end)
        val parsedResponse = response?.let { parser.parse(it) }
        return parsedResponse?.let { readAllPolylineFromSteps(it.routes[0].legs[0].steps) }
    }

    private fun readAllPolylineFromSteps(steps: List<GoogleSteps>) : List<LatLng> {
        return steps.flatMap {
            polylineDecoder.decode(it.polyline.points)
        }
    }
}
