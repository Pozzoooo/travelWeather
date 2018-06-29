package pozzo.apps.travelweather

import android.app.Application
import com.splunk.mint.Mint

/**
 * TODO
 *
 * Minor bug: select random start position, and then press my current position fab, it will show a
 *  route not found dialog, which it should not.
 *
 * Minor bug: Multiples clicks on curret location make it request multiple times even if the last one
 *  has not finished yet
 *
 * Minor bug: Channel keep flowing after changing route destination.
 *
 * Translate to Spanish
 * Fix animation of the flag going back to the right
 *
 * I believe with my current implementation I could make much more use of background work, what about try coroutines?
 * Feedback Lisa: Developer, please add departure times to this app
 * Feedback Paulo: O App podia permitir destinos múltiplos, como o maps permite... assim ficaria mais completo. Estender a precisão para mais dias também seria interessante.
 * Build route if it was triggered when no connection was available (job schedule?)
 * Agendar uma viagem
 * Notificar quando o tempo mudar apos ter agendado uma viagem
 * A distancia entre previsao deve ser dinamica, em uma distancia maior eu nao precio de tantas previsoes.
 * Realizar a separacao early, late...
 * A questao do horario de saida e chegada, como fica?
 * Outra fonte para busca de previsao do tempo
 * Unit test :)
 * Animar as flags voltando para a lateral quando apertar o clear
 * I need to make my dependencies more explicity, I guess unit test and dagger will help me on that
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (!BuildConfig.DEBUG) Mint.initAndStartSession(this, "c315b759")
    }
}

/**
 * Yahoo weather, 2,000 requisicoes por dia.
 * https://developer.yahoo.com/weather/
 * Google direction = 2,500 free directions requests per day and $0.50 USD / 1000 additional requests, up to 100,000 daily.
 * https://developers.google.com/maps/documentation/directions/usage-limits
 */
