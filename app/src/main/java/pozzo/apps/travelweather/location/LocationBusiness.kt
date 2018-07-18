package pozzo.apps.travelweather.location

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.location.helper.GMapV2Direction
import java.io.IOException
import java.util.*

/**
 * Controla regra de negocio de localizacao.
 */
class LocationBusiness {

    /**
     * @return Posicao atual do usuario.
     */
    @Throws(SecurityException::class)
    fun getCurrentKnownLocation(context: Context): Location? =
            (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?)?.let {
                val bestProvider = it.getBestProvider(Criteria(), false) ?: throw SecurityException("No providers found")
                it.getLastKnownLocation(bestProvider)
            }

    /**
     * Rota para o dado destino.
     */
    @Throws(IOException::class)
    fun getDirections(startPosition: LatLng, finishPosition: LatLng): ArrayList<LatLng>? {
        val directionV2 = GMapV2Direction()
        return directionV2.getDocument(startPosition, finishPosition)?.let { directionV2.getDirection(it) }
    }
}
