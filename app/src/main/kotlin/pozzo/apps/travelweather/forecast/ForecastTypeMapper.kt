package pozzo.apps.travelweather.forecast

interface ForecastTypeMapper {

    fun getForecastType(type: String) : ForecastType
}
