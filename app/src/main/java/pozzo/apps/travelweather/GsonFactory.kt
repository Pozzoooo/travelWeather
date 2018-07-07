package pozzo.apps.travelweather

import com.google.gson.Gson
import com.google.gson.GsonBuilder

import java.lang.reflect.Modifier

/**
 * Configure and center ao Gson instance.
 */
object GsonFactory {
    val gson : Gson by lazy {
        GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create()
    }
}
