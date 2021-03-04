package pozzo.apps.travelweather

import android.app.Application
import kotlinx.coroutines.Dispatchers
import pozzo.apps.travelweather.common.Android
import pozzo.apps.travelweather.common.Util
import pozzo.apps.travelweather.core.CoroutineSettings
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.core.bugtracker.FirebaseBug
import pozzo.apps.travelweather.core.bugtracker.LogBug
import pozzo.apps.travelweather.core.injection.AppComponent
import pozzo.apps.travelweather.core.injection.AppModule
import pozzo.apps.travelweather.core.injection.DaggerAppComponent
import pozzo.apps.travelweather.core.injection.NetworkModule
import pozzo.apps.travelweather.forecast.ForecastModuleAll

/**
 * TODO
 *
 * Anyway to track a specific time?
 *
 * Minor bug: Multiples clicks on current location make it request multiple times even if the last one
 *  has not finished yet
 *
 * Minor bug: Channel keep flowing after changing route destination.
 *
 * Feedback Vassilis: Manual override for temperature scale
 * Feedback Lisa: Developer, please add departure times to this app
 * Feedback Paulo: O App podia permitir destinos múltiplos, como o maps permite... assim ficaria mais completo. Estender a precisão para mais dias também seria interessante.
 * Feedback Pedro: Wind speed (good for motorcycle trip)
 * Feedback Dwight: 10 day forecast at once.
 * Feedback Kathie: Multiplas rotas.
 * Feedback Kathie: Save route for future use.
 *
 * I can add distance and time with new fields from Direction
 *
 * Should I create some espresso tests for integration?
 * 
 * Mover mapa quando estiver arrastando e atinger o canto do mapa
 * Animar as flags voltando para a lateral quando apertar o clear
 * I might need to better hide some keys? (Google maps, Firebase, Mint)
 * I need to reflect about the business layer, does this naming makes sense? It somehow fits too much, I feel like I need more specific namings.
 * Add more days, after after tomorrow, but I need to think in a proper way ot display it
 * Build route if it was triggered when no connection was available (job schedule?)
 * Agendar uma viagem
 * Notificar quando o tempo mudar apos ter agendado uma viagem
 * A distancia entre previsao deve ser dinamica, em uma distancia maior eu nao precio de tantas previsoes.
 * Realizar a separacao early, late...
 * Outra fonte para busca de previsao do tempo
 * Finish Dagger refactoring
 * Increase test coverage
 *
 * Another option might be https://www.worldweatheronline.com/developer/api/pricing2.aspx
 * Br apenas
 * http://servicos.cptec.inpe.br/XML/
 */
class App : Application() {
    companion object {
        private lateinit var appComponent: AppComponent

        fun component(): AppComponent {
            return appComponent
        }

        fun setComponent(component: AppComponent) {
            appComponent = component
        }
    }

    override fun onCreate() {
        super.onCreate()
        initBugTracker()
        initComponent()
        initCoroutines()
        initUtil()
	}

    private fun initBugTracker() {
        val bugInstance = if (BuildConfig.DEBUG) {
            LogBug()
        } else {
            FirebaseBug()
        }
        Bug.setInstance(bugInstance)
    }

    private fun initComponent() {//todo pq eu nao preciso de todos os compoenents aqui? E da pra usar da forma q usamos no trampo?
        setComponent(DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .networkModule(NetworkModule())
                .forecastModule(ForecastModuleAll())
                .build()
        )
    }

    private fun initCoroutines() {
        CoroutineSettings.background = Dispatchers.Default
        CoroutineSettings.ui = Dispatchers.Main
    }

    private fun initUtil() {
        Util.instance = Android()
    }
}
