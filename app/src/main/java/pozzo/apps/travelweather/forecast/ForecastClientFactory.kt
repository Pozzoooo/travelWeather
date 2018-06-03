package pozzo.apps.travelweather.forecast

import pozzo.apps.travelweather.forecast.yahoo.ForecastClientYahoo

class ForecastClientFactory {

  companion object {
    val instance: ForecastClientFactory = ForecastClientFactory()
  }

  fun getForecastClient() : ForecastClient {
    return ForecastClientYahoo()
  }
}
