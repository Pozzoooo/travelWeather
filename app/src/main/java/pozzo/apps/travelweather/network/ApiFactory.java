package pozzo.apps.travelweather.network;

import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;

import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedByteArray;

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
                    .setErrorHandler(new HandleError())
                    .setConverter(new GsonConverter(new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create()))
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

    public static class HandleError implements ErrorHandler {
        @Override
        public Throwable handleError(RetrofitError cause) {
            if(cause.getCause() != null)
                cause.getCause().printStackTrace();
            System.out.println(
                    new String(((TypedByteArray) cause.getResponse().getBody()).getBytes()));
            return null;
        }
    }
}
