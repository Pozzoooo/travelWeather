package pozzo.apps.travelweather.forecast.darksky

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

/**
 * https://darksky.net/dev/account
 */
interface DarkSkyApi {
    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("{latitude},{longitude}?exclude=currently,minutely,hourly,alerts,flags")//&units=si
    fun forecast(@Path("latitude") latitude: Double, @Path("longitude") longitude: Double): Call<ResponseBody>
}
