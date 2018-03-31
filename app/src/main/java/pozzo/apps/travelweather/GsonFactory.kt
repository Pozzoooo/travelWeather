package pozzo.apps.travelweather

import com.google.gson.Gson
import com.google.gson.GsonBuilder

import java.lang.reflect.Modifier

/**
 * Configure and center ao Gson instance.
 */
object GsonFactory {
    private var gson: Gson? = null

    /**
     * @return Get our default gson implementation.
     */
    fun getGson(): Gson {
        if (gson == null) {
            gson = GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                    .serializeNulls()
                    .create()
        }
        return gson!!
    }
}
