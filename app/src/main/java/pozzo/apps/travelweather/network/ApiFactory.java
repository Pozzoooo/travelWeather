package pozzo.apps.travelweather.network;

import pozzo.apps.travelweather.helper.GsonFactory;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

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
            yahooWeather = new RestAdapter.Builder()
                    .setEndpoint("https://query.yahooapis.com")
                    .setRequestInterceptor(new SendInterceptor())
                    .setConverter(new GsonConverter(GsonFactory.getGson()))
                            .build()
                            .create(YahooWeather.class);
        }
        return yahooWeather;
    }

    /**
     * General handling before sending a request.
     */
    private static class SendInterceptor implements RequestInterceptor {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Accept", "application/json");
        }
    }
}
