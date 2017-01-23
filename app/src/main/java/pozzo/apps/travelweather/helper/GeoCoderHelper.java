package pozzo.apps.travelweather.helper;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Helps in location.
 */
public class GeoCoderHelper {
	private Context ctx;

	public GeoCoderHelper(Context ctx) {
		this.ctx = ctx;
	}

	/**
	 * Standard way to convert an address to a String.
	 * 
	 * @param addr
	 * @return
	 */
	public String asString(Address addr) {
		if(addr.getMaxAddressLineIndex() <= 0) {
			return "";
		}

		StringBuilder currentAddr = new StringBuilder();
		for (int i=0; i<addr.getMaxAddressLineIndex(); ++i) {
			currentAddr.append(addr.getAddressLine(i));
			currentAddr.append(", ");
		}
		currentAddr.append(addr.getCountryName());

		return currentAddr.toString();
	}

	/**
	 * Get related address to the position.
	 * 
	 * @param point
	 * @return
	 */
	public String getAddress(LatLng point) {
		Geocoder geoCoder = new Geocoder(ctx, Locale.getDefault());
		String addr = "";
		try {
			List<Address> addresses = geoCoder.getFromLocation(
					point.latitude,
					point.longitude, 1);

			if (addresses.size() > 0)
				addr = asString(addresses.get(0));
		}
		catch (IOException e) {                
			e.printStackTrace();
		}
		return addr;
	}

	/**
	 * Get a list of address similar to the source.
	 * 
	 * @param address
	 * @return
	 */
	public List<Address> getSimilarAddresses(String address) throws IOException {
		Geocoder geoCoder = new Geocoder(ctx, Locale.getDefault()); 
		List<Address> addresses = null;
		addresses = geoCoder.getFromLocationName(address, 5);

		return addresses;
	}

	/**
	 * Get address location used to use at MapView.
	 * 
	 * @param address
	 * @return
	 */
	public LatLng getPosition(Address address) {
		return new LatLng(address.getLatitude(), address.getLongitude());
	}

	/**
	 * Get first related address.
	 * 
	 * @param address
	 * @return
	 */
	public LatLng getPositionFromFirst(String address) throws IOException {
		List<Address> addresses = getSimilarAddresses(address);
		if(addresses == null || addresses.size() == 0)
			return null;

		return getPosition(addresses.get(0));
	}
}
