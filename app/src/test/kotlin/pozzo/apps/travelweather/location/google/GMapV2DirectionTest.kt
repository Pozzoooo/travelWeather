package pozzo.apps.travelweather.location.google

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Before
import org.junit.Test
import pozzo.apps.travelweather.BuildConfig
import java.util.concurrent.TimeUnit

class GMapV2DirectionTest {
    private lateinit var directionBusiness: GMapV2Direction

    @Before fun setup() {
        val logging = HttpLoggingInterceptor()
        logging.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        val okHttp = OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .addInterceptor(logging)
                .build()

        directionBusiness = GMapV2Direction(okHttp, Gson())
    }

    @Test fun assertReturnAsExpected() {
        val direction = directionBusiness.getDirection(LatLng(53.374153, -6.164832), LatLng(53.376611, -6.169778))
        println(direction.toString())
    }
}
