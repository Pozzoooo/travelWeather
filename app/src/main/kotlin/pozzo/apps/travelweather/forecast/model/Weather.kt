package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.map.model.Address

data class Weather(
    val url: String,
    val forecasts: List<Forecast>,
    val address: Address,
    val poweredBy: PoweredBy) {

    val latLng: LatLng
        get() = address.latLng

    //Passo 2 - E ai ao inves de passar o dia eu passo timestamp e pego o proximo
    fun getForecast(day: Day): Forecast {
        val index = day.index
        return if (index < 0 || index >= forecasts.size) {
            Bug.get().logException(ArrayIndexOutOfBoundsException(
                    "Forecast out of range, tried: $index, but size was ${forecasts.size}"))
            forecasts.last()
        } else {
            forecasts.getOrNull(day.index) ?: forecasts.last()
        }
    }
}

//Passo 3 - E posso deixar o fato da progressao pela rota como um proximo passo?
//Mas uma forma bem simples seria pegar sempre o proximo...
//Passo 4+ - E num futuro ainda mais distante da pra tentar usar o google direction para uma precisao de tempo melhor