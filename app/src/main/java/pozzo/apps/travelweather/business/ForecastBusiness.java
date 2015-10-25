package pozzo.apps.travelweather.business;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import pozzo.apps.travelweather.helper.GeoCoderHelper;
import pozzo.apps.travelweather.model.Address;
import pozzo.apps.travelweather.model.Forecast;
import pozzo.apps.travelweather.model.Weather;
import pozzo.apps.travelweather.network.ApiFactory;
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
    public Weather from(LatLng location, Context context) {
        String locationStr = new GeoCoderHelper(context).getAddress(location);
        Address address = new Address();
        address.setAddress(locationStr);
        address.setLatitude(location.latitude);
        address.setLongitude(location.longitude);
        return from(address);
    }

    /**
     * Forecast from given location.
     */
    public Weather from(Address address) {
        //item = condition + forecast
        //and u='c' - Serve para pegar temperatura em celsius
        String query = "select item from weather.forecast where woeid in " +
                "(select woeid from geo.places(1) where text=\"" + address.getAddress() + "\") and u='c'";
        Response response = ApiFactory.getInstance().getYahooWather().forecast(query);
        String result = new String(((TypedByteArray) response.getBody()).getBytes());
        JsonObject jsonResult = new JsonParser().parse(result).getAsJsonObject();
        JsonObject channel = jsonResult
                .getAsJsonObject("query")
                .getAsJsonObject("results")
                .getAsJsonObject("channel");

        JsonArray forecastArray = channel
                .getAsJsonObject("item")
                .getAsJsonArray("forecast");
        Forecast[] forecasts = new Gson().fromJson(forecastArray, Forecast[].class);
        if(forecasts == null || forecasts.length <= 0)
            return null;

        Weather weather = new Weather();
        weather.setAddress(address);
        weather.setForecasts(forecasts);
        return weather;
    }
}
