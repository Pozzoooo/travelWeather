package pozzo.apps.travelweather.forecast.yahoo

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * Apenas o tempo atual
 * http://www.webservicex.net/WS/WSDetails.aspx?CATID=12&WSID=56
 *
 * Br apenas
 * http://servicos.cptec.inpe.br/XML/
 *
 * Yahoo, mas tem limitacao de 2000 por dia
 * https://query.yahooapis.com/v1/public/yql?
 * q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20
 * (select%20woeid%20from%20geo.places(1)%20where%20text%3D%22nome%2C%20ak%22)
 * &format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys
 *
 * Created by soldier on 10/5/15.
 */
interface YahooWeather {
    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("/v1/public/yql?format=json&env=store://datatables.org/alltableswithkeys")
    fun forecast(@Query("q") q: String): Call<ResponseBody>
}
