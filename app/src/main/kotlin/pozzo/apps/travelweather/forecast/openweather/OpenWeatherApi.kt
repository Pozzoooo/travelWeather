package pozzo.apps.travelweather.forecast.openweather

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * https://openweathermap.org/current#geo
 */
interface OpenWeatherApi {
    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("data/2.5/forecast")
    fun forecast(@Query("lat") latitude: Double, @Query("lon") longitude: Double,
                 @Query("appid") key: String): Call<ResponseBody>
}
