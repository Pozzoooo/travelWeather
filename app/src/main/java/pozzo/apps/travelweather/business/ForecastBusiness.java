package pozzo.apps.travelweather.business;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import pozzo.apps.travelweather.helper.GeoCoderHelper;
import pozzo.apps.travelweather.model.Forecast;
import pozzo.apps.travelweather.network.ApiFactory;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by sarge on 10/19/15.
 */
public class ForecastBusiness {

    /**
     * Forecast from given location.
     */
    public Forecast from(LatLng location, Context context) {
        String address = new GeoCoderHelper(context).getAddress(location);
        return from(address);
    }

    /**
     * Forecast from given location.
     */
    public Forecast from(String location) {
        //TODO acredito que nao precismos de Select * =]
        String query = "select * from weather.forecast where woeid in " +
                "(select woeid from geo.places(1) where text=\"" + location + "\")";
        Response response = ApiFactory.getInstance().getYahooWather().forecast(query);
        String result = new String(((TypedByteArray) response.getBody()).getBytes());
        JsonObject jsonResult = new JsonParser().parse(result).getAsJsonObject();
        JsonArray forecastArray = jsonResult
                .getAsJsonObject("query")
                .getAsJsonObject("results")
                .getAsJsonObject("channel")
                .getAsJsonObject("item")
                .getAsJsonArray("forecast");
        Forecast[] forecasts = new Gson().fromJson(forecastArray, Forecast[].class);
        if(forecasts != null && forecasts.length > 0)
            return forecasts[0];
        return null;
    }
}
