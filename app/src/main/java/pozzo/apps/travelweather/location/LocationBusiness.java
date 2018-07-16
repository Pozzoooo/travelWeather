package pozzo.apps.travelweather.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;

import pozzo.apps.travelweather.location.helper.GMapV2Direction;

/**
 * Controla regra de negocio de localizacao.
 */
public class LocationBusiness {

    /**
     * @return Posicao atual do usuario.
     */
    public Location getCurrentKnownLocation(Context context) throws SecurityException {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
			String bestProvider = locationManager.getBestProvider(new Criteria(), false);
			if (bestProvider == null)
				throw new SecurityException("No providers found");

			return locationManager.getLastKnownLocation(bestProvider);
		}
		return null;
    }

    /**
     * Rota para o dado destino.
     */
    public ArrayList<LatLng> getDirections(@NonNull LatLng startPosition, @NonNull LatLng finishPosition) throws IOException {
        GMapV2Direction md = new GMapV2Direction();
        Document doc = md.getDocument(startPosition, finishPosition);
        return doc == null ? null : md.getDirection(doc);
    }
}
