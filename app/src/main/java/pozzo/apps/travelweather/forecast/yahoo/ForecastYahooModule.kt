package pozzo.apps.travelweather.forecast.yahoo

import pozzo.apps.travelweather.forecast.ForecastClient
import pozzo.apps.travelweather.forecast.ForecastModule

class ForecastYahooModule : ForecastModule() {

    override fun forecastClient(): ForecastClient = ForecastClientYahoo()
}
