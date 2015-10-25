package pozzo.apps.travelweather.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;

/**
 * Configure and center ao Gson instance.
 *
 * Created by sarge on 10/25/15.
 */
public class GsonFactory {
    private static Gson gson;

    /**
     * @return Get our default gson implementation.
     */
    public static Gson getGson() {
        if(gson == null) {
            gson = new GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                    .serializeNulls()
                    .create();
        }
        return gson;
    }
}
