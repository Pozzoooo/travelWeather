package pozzo.apps.travelweather.business;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.splunk.mint.Mint;

import pozzo.apps.travelweather.exception.AddressNotFoundException;
import pozzo.apps.travelweather.helper.GeoCoderHelper;
import pozzo.apps.travelweather.helper.GsonFactory;
import pozzo.apps.travelweather.model.Address;
import pozzo.apps.travelweather.model.Forecast;
import pozzo.apps.travelweather.model.Weather;
import pozzo.apps.travelweather.network.ApiFactory;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Forecast business logic.
 *
 * Created by sarge on 10/19/15.
 */
public class ForecastBusiness {

    /**
     * Forecast from given location.
     */
    public Weather from(LatLng location, Context context) throws AddressNotFoundException {
        if(location == null || context == null)
            return null;

        String locationStr = new GeoCoderHelper(context).getAddress(location);
        if(locationStr == null || locationStr.isEmpty())
            throw new AddressNotFoundException();

        Address address = new Address();
        address.setAddress(locationStr);
        address.setLatitude(location.latitude);
        address.setLongitude(location.longitude);
        return from(address);
    }

	public Weather from(Address address) {
		int maxRetries = 5, i = 0;
		String addressStr = address.getAddress();
		if (addressStr == null)
			return null;
		System.out.println("before discard: " + addressStr);
		do {
			try {

				if (!addressStr.contains(","))
					return null;

				Weather weather = fromInternal(addressStr);
				if (weather == null)
					return null;

				System.out.println("Worked with: " + addressStr);
				weather.setAddress(address);
				return weather;
			} catch (Exception e) {
				//ignored to retrie
			}
			System.out.println("Failed with: " + addressStr);
			int firstCommaIdx = addressStr.indexOf(",");
			addressStr = firstCommaIdx == -1 ? "" : addressStr.substring(firstCommaIdx + 1).trim();
		} while (++i < maxRetries);
		return null;
	}

    /**
     * Forecast from given location.
     */
    private Weather fromInternal(String address) throws Exception {
        //item = condition + forecast
        //and u='c' - Serve para pegar temperatura em celsius
        String query = "select item from weather.forecast where woeid in " +
                "(select woeid from geo.places(1) where text=\"" + address + "\") and u='c'";
		Response response;
		try {
			response = ApiFactory.getInstance().getYahooWather().forecast(query);
		} catch(RetrofitError | IllegalStateException e) {
			Mint.logExceptionMessage("query", query, e);
			throw e;
		}
        String result = new String(((TypedByteArray) response.getBody()).getBytes());
        try {
            JsonObject jsonResult = new JsonParser().parse(result).getAsJsonObject();
            JsonObject channel = jsonResult
                    .getAsJsonObject("query")
                    .getAsJsonObject("results")
                    .getAsJsonObject("channel");
            JsonObject item = channel.getAsJsonObject("item");
            JsonArray forecastArray = item.getAsJsonArray("forecast");
            Gson gson = GsonFactory.getGson();
            Forecast[] forecasts = gson.fromJson(forecastArray, Forecast[].class);
            if (forecasts == null || forecasts.length <= 0)
                return null;

            Weather weather = new Weather();
            weather.setForecasts(forecasts);
            weather.setUrl(item.get("link").getAsString());
            return weather;
        } catch (ClassCastException e) {
            Mint.logExceptionMessage("result", result, e);
            throw e;
        }
    }
}
