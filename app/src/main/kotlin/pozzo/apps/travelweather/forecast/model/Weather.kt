package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.LatLng
import pozzo.apps.travelweather.map.model.Address
import java.util.*

data class Weather(
    val url: String,
    val forecasts: List<Forecast>,
    val address: Address,
    val poweredBy: PoweredBy) {

    companion object {
        private const val PAST_LIMIT = 2L * 60L * 60L * 1000L
    }

    val latLng: LatLng
        get() = address.latLng

    fun getForecast(date: Calendar): Forecast {
        var closer: Forecast? = null
        forecasts.forEach {
            val isInClosePastOrFuture = date.timeInMillis < it.dateTime.timeInMillis + PAST_LIMIT
            val isEvenCloser = it.dateTime.timeInMillis < closer?.dateTime?.timeInMillis ?: Long.MAX_VALUE
            if (isInClosePastOrFuture && isEvenCloser) {
                closer = it
            }
        }
        return closer ?: forecasts.last()
    }
}

//Passo 3 - E posso deixar o fato da progressao pela rota como um proximo passo?
//Mas uma forma bem simples seria pegar sempre o proximo...
//Passo 4+ - E num futuro ainda mais distante da pra tentar usar o google direction para uma precisao de tempo melhor
