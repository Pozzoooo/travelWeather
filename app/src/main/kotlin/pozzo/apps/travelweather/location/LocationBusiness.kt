package pozzo.apps.travelweather.location

import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.location.google.GMapV2Direction
import java.io.IOException

/**
 * Controla regra de negocio de localizacao.
 */
class LocationBusiness(private val directionParser: GMapV2Direction) {

    /**
     * @return Posicao atual do usuario.
     */
    @Throws(SecurityException::class)
    fun getCurrentKnownLocation(locationManager: LocationManager?): Location? =
            locationManager?.let {
                val bestProvider = it.getBestProvider(Criteria(), false) ?: throw SecurityException("No providers found")
                it.getLastKnownLocation(bestProvider)
            }

    /**
     * Rota para o dado destino.
     */
    @Throws(IOException::class)
    fun getDirections(startPosition: LatLng, finishPosition: LatLng): List<LatLng>? {
        return directionParser.getDirection(startPosition, finishPosition)
    }
}
