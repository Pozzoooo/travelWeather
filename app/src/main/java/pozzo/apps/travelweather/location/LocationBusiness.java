package pozzo.apps.travelweather.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;

import java.util.ArrayList;

import pozzo.apps.travelweather.location.helper.GMapV2Direction;

/**
 * Controla regra de negocio de localizacao.
 */
public class LocationBusiness {

    /**
     * @return Posicao atual do usuario.
     */
    public Location getCurrentLocation(Context context) throws SecurityException {
        LocationManager locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        String bestProvider = locationManager.getBestProvider(new Criteria(), false);
        if (bestProvider == null)
        	throw new SecurityException("No providers found");

		return locationManager.getLastKnownLocation(bestProvider);
    }

    /**
     * Rota para o dado destino.
     */
    public ArrayList<LatLng> getDirections(LatLng startPosition, LatLng finishPosition) {
        if(startPosition == null || finishPosition == null)
            return null;

        GMapV2Direction md = new GMapV2Direction();
        Document doc = md.getDocument(
                startPosition, finishPosition, GMapV2Direction.MODE_DRIVING);
        return doc == null ? null : md.getDirection(doc);
    }
}
