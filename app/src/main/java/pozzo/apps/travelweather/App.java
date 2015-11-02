package pozzo.apps.travelweather;

import com.activeandroid.app.Application;

/**
 * TODO
 *  1x
 *  Adicionar Mint.
 *  Criar chave release para mapa.
 *
 *  ?
 *  Realizar a separacao eraly, late...
 *  A questao do horario de saida e chegada, como fica?
 *  No lugar de deixar no menu, talvez eu possa colocar como titulo, o hoje, amanha e tal...
 *
 *  2x
 *  Tratamento yahoo null com reenvio de cidade
 *  Persistir dados
 *  Agendar uma viagem
 *  Exibir ultima rota que estava sendo visualizada atraves do banco
 *  Notificar quando o tempo mudar apos ter agendado uma viagem
 *  Outra fonte para busca de previsao do tempo
 *  Menu a cada quantos km realiar a previsao
 *  Alterar previsao principal de hoje, amanha, depois etc
 *  Utilizar ExecutorService para requisicao de previsao
 *
 * Created by sarge on 10/19/15.
 */
public class App extends Application {
}

/**
 * Yahoo weather, 2,000 requisicoes por dia.
 * 		https://developer.yahoo.com/weather/
 * Google direction = 2,500 free directions requests per day and $0.50 USD / 1000 additional requests, up to 100,000 daily.
 * 		https://developers.google.com/maps/documentation/directions/usage-limits
 */
