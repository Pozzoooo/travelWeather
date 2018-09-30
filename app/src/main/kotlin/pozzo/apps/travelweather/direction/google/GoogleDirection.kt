package pozzo.apps.travelweather.direction.google

import com.google.android.gms.maps.model.LatLng

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
