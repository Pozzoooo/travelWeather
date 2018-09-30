package pozzo.apps.travelweather.location

import android.location.Criteria
import android.location.Location
import android.location.LocationManager

/**
 * Controla regra de negocio de localizacao.
 */
class LocationBusiness {

    /**
     * @return Posicao atual do usuario.
     */
    @Throws(SecurityException::class)
    fun getCurrentKnownLocation(locationManager: LocationManager?): Location? =
            locationManager?.let {
                val bestProvider = it.getBestProvider(Criteria(), false) ?: throw SecurityException("No providers found")
                it.getLastKnownLocation(bestProvider)
            }
}
