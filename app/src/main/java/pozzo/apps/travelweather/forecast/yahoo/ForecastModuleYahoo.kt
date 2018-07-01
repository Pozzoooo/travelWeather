package pozzo.apps.travelweather.forecast.yahoo

import pozzo.apps.travelweather.forecast.ForecastClient
import pozzo.apps.travelweather.forecast.ForecastModule
import pozzo.apps.travelweather.forecast.ForecastTypeMapper

class ForecastModuleYahoo : ForecastModule() {

    override fun forecastClient(): ForecastClient = ForecastClientYahoo()
    override fun forecastTypeMapper(): ForecastTypeMapper = ForecastTypeMapperYahoo()
}
