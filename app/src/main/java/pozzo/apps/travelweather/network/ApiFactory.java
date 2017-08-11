package pozzo.apps.travelweather.network;

import pozzo.apps.travelweather.helper.GsonFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Creates ou APIs abstraction to communicate with server.
 *
 * Created by ghost on 06/09/15.
 */
public class ApiFactory {
    private static final ApiFactory instance = new ApiFactory();
    private static YahooWeather yahooWeather;

    private ApiFactory() {}

    public static ApiFactory getInstance() {
        return instance;
    }

    public YahooWeather getYahooWather() {
        if(yahooWeather == null) {
            yahooWeather = new Retrofit.Builder()
                    .baseUrl("https://query.yahooapis.com")
                    .addConverterFactory(GsonConverterFactory.create(GsonFactory.getGson()))
					.build()
					.create(YahooWeather.class);
        }
        return yahooWeather;
    }
}
