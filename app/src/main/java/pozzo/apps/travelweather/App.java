package pozzo.apps.travelweather;

import com.activeandroid.app.Application;
import com.splunk.mint.Mint;

/**
 * TODO
 *  1x
 3- Se o usuário pesquisar, e depois decidir selecionar um ponto tocando na tela, esconder a caixa texto da pesquisa;
 5- Após ter traçado uma rota, se o usuário pressionar o botão voltar, deixar no estado inicial do app, posicionando apenas na origem. Se pressionar novamente, ai sim fecha o aplicativo;
 *	Clarificar de alguma forma as acoes do usuario, tipo arrastar o marcador talvez.
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
