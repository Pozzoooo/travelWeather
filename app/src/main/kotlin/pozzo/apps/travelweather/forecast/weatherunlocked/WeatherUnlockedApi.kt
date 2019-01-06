package pozzo.apps.travelweather.forecast.weatherunlocked

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * https://developer.weatherunlocked.com/buyer/stats
 * https://developer.weatherunlocked.com/documentation/localweatherZ
 */
interface WeatherUnlockedApi {

    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("forecast/{latitude},{longitude}")
    fun forecast(@Path("latitude") latitude: Double, @Path("longitude") longitude: Double,
                 @Query("app_id") appId: String, @Query("app_key") appKey: String): Call<ResponseBody>
}
