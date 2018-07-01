package pozzo.apps.travelweather.forecast.yahoo

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [YahooModule::class])
interface YahooComponent {

    fun yahooWeather() : YahooWeather
    fun inject(forecastClientYahoo: ForecastClientYahoo)
}
