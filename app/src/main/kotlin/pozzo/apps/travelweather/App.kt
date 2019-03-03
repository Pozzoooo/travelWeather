package pozzo.apps.travelweather

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.Main
import pozzo.apps.travelweather.core.CoroutineSettings
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.core.bugtracker.FabricBug
import pozzo.apps.travelweather.core.bugtracker.LogBug
import pozzo.apps.travelweather.core.injection.AppComponent
import pozzo.apps.travelweather.core.injection.AppModule
import pozzo.apps.travelweather.core.injection.DaggerAppComponent
import pozzo.apps.travelweather.core.injection.NetworkModule
import pozzo.apps.travelweather.forecast.ForecastModuleAll

/**
 * TODO
 *
 * Ideia: Colocar limite de request de previsao do tempo, se o usuario atingir o limite
 *  posso criar um sistema de in app para comprar adicionais
 * Que podem acumular e descontar sempre q usar alem do limite
 *
 * Another option might be https://openweathermap.org/price
 * https://www.worldweatheronline.com/developer/api/pricing2.aspx
 *
 * Br apenas
 * http://servicos.cptec.inpe.br/XML/
 *
 * Minor bug: Multiples clicks on current location make it request multiple times even if the last one
 *  has not finished yet
 *
 * Minor bug: Channel keep flowing after changing route destination.
 *
 * Feedback Lisa: Developer, please add departure times to this app
 * Feedback Paulo: O App podia permitir destinos múltiplos, como o maps permite... assim ficaria mais completo. Estender a precisão para mais dias também seria interessante.
 *
 * Make the number of weather dynamic based on the size of the route.
 *  So if the route is really big, then I add fewer weather requests as they probably matter less anyway.
 *  At some point it can even turn into a configuration for advanced users.
 *  Important class regarding to it: DirectionWeatherFilter
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
 */
class App : Application() {
    companion object {
        private var appComponent: AppComponent? = null

        fun component(): AppComponent {
            return appComponent!!//It should be initiated
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
    }

    private fun initBugTracker() {
        val bugInstance = if (BuildConfig.DEBUG) {
            LogBug()
        } else {
            FabricBug()
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
}
