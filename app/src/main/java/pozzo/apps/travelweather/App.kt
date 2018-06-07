package pozzo.apps.travelweather

import android.app.Application
import com.splunk.mint.Mint

/**
 * TODO
 * Minor bug, select random start position, and then press my current position fab, it will show a
 *  route not found dialog, which it should not.
 *
 * The after tomorrow selection was not a bug, but a auto backup feature.
 *  - But this show me more the need to make the day selection more clear, should come soon
 *
 * O que acha de parar e diagramar tudo?
 *  - Sera que isso daria uma visao melhor?
 *  - Sera que pode me ajudar na manutencao no futuro?
 *
 * What about a tutorial on the menu? quick one
 *
 * Build route if it was triggered when no connection was available (job schedule?)
 * Tutorial
 * Agendar uma viagem
 * Notificar quando o tempo mudar apos ter agendado uma viagem
 * A distancia entre previsao deve ser dinamica, em uma distancia maior eu nao precio de tantas previsoes.
 * Realizar a separacao early, late...
 * A questao do horario de saida e chegada, como fica?
 * Outra fonte para busca de previsao do tempo
 * Unit test :)
 * Animar as flags voltando para a lateral quando apertar o clear
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
