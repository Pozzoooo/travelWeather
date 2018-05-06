package pozzo.apps.travelweather

import android.app.Application
import com.splunk.mint.Mint

/**
 * TODO
 * Agendar uma viagem
 * Notificar quando o tempo mudar apos ter agendado uma viagem
 * A distancia entre previsao deve ser dinamica, em uma distancia maior eu nao precio de tantas previsoes.
 * Realizar a separacao early, late...
 * A questao do horario de saida e chegada, como fica?
 * Outra fonte para busca de previsao do tempo
 * Unit test :)
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Mint.initAndStartSession(this, "c315b759")
    }
}

/**
 * Yahoo weather, 2,000 requisicoes por dia.
 * https://developer.yahoo.com/weather/
 * Google direction = 2,500 free directions requests per day and $0.50 USD / 1000 additional requests, up to 100,000 daily.
 * https://developers.google.com/maps/documentation/directions/usage-limits
 */
