package pozzo.apps.travelweather.business;

import com.google.android.gms.maps.model.LatLng;

import pozzo.apps.travelweather.model.Address;
import pozzo.apps.travelweather.model.Weather;

/**
 * Forecast business logic.
 *
 * Created by sarge on 10/19/15.
 */
public class ForecastBusiness {
	private final int MAX_RETRIES = 3;

	private ForecastClient forecastClient = new ForecastClientYahoo();

    /**
     * Forecast from given location.
     */
    public Weather from(LatLng location) {
		return forecastClient.fromCoordinates(location);
    }

	public Weather from(Address address) {
		int i = 0;
		String addressStr = address.getAddress();
		if (addressStr == null)
			return null;
		do {
			try {
				if (!addressStr.contains(","))
					return null;

				Weather weather = forecastClient.fromAddress(addressStr);
				weather.setAddress(address);
				return weather;
			} catch (Exception e) {
				//ignored to retrie
			}
			int firstCommaIdx = addressStr.indexOf(",");
			addressStr = firstCommaIdx == -1 ? "" : addressStr.substring(firstCommaIdx + 1).trim();
		} while (++i < MAX_RETRIES);
		return null;
	}
}
