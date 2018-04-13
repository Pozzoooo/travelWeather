package pozzo.apps.travelweather.forecast.yahoo

import pozzo.apps.travelweather.GsonFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiFactory private constructor() {
    companion object {
        val instance = ApiFactory()
    }

    val yahooWeather: YahooWeather = Retrofit.Builder()
            .baseUrl("https://query.yahooapis.com")
            .addConverterFactory(GsonConverterFactory.create(GsonFactory.getGson()))
            .build()
            .create(YahooWeather::class.java)
}
