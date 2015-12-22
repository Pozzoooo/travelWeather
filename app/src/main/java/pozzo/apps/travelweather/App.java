package pozzo.apps.travelweather;

import com.activeandroid.app.Application;
import com.splunk.mint.Mint;

/**
 * TODO
 *  1x
 *	Clarificar de alguma forma as acoes do usuario, tipo arrastar o marcador talvez.
 *  Criar tratamento para quando nao conseguir pegar a localizacao atual, talvez seja uma boa ateh
 *      adicioar um spinner e ativar a requisicao de localizacao atravez de um callback, porem,
 *      eh importante colocar um timeout (como faz mesmo?).
 *  Busca na action bar, e a selecao de dia (hoje, amananha...) pode ir em uma spinner no estilo
 *      navegacao.
 *  Quando houver um erro na criacao da rota (no estilo reportado do celular do pai) fazer um clear
 *      para garantir que nao eh a posicao inicial o problema.
 *  A distancia entre previsao deve ser dinamica, em uma distancia maior eu nao precio de tantas
 *      previsoes.
 *
 *  ?
 *  Realizar a separacao early, late...
 *  A questao do horario de saida e chegada, como fica?
 *
 *  2x
 *  Tratamento yahoo null com reenvio de cidade
 *  Persistir dados
 *  Agendar uma viagem
 *  Exibir ultima rota que estava sendo visualizada atraves do banco
 *  Notificar quando o tempo mudar apos ter agendado uma viagem
 *  Outra fonte para busca de previsao do tempo
 *  Menu a cada quantos km realizar a previsao
 *  Utilizar ExecutorService para requisicao de previsao
 *
 * Created by sarge on 10/19/15.
 */
public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Mint.initAndStartSession(this, "c315b759");
	}
}

/**
 * Yahoo weather, 2,000 requisicoes por dia.
 * 		https://developer.yahoo.com/weather/
 * Google direction = 2,500 free directions requests per day and $0.50 USD / 1000 additional requests, up to 100,000 daily.
 * 		https://developers.google.com/maps/documentation/directions/usage-limits
 */
