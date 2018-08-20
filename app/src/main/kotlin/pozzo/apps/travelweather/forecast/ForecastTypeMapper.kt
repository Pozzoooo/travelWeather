package pozzo.apps.travelweather.forecast

import pozzo.apps.travelweather.forecast.model.Forecast

interface ForecastTypeMapper {

    fun getForecastType(forecast: Forecast) : ForecastType
}
