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
 * Lots of requests for additional waypoints
 * Feedback Kathie: Multiplas rotas.
 * Feedback Paulo: O App podia permitir destinos múltiplos, como o maps permite... assim ficaria mais completo. Estender a precisão para mais dias também seria interessante.
 * Parece uma boa dar um offset pra cima no icone da bandeira pra faclitar a colocacao dela
 *
 * A better loading animation?
 *
 * Inverter rota
 *
 * Would be nice to create our own weather details, so we can make it 100% consistent with APIs
 *
 * Improvements on design would be welcome
 *
 * Minor bug: Channel keep flowing after changing route destination.
 *
 * Feedback Vassilis: Manual override for temperature scale
 * Feedback Pedro: Wind speed (good for motorcycle trip)
 * Feedback Dwight: 10 day forecast at once.
 * Feedback Kathie: Save route for future use.
 * Feedback Egor: Multiple day trip planning.
 * Feedback Günther: Vehicle profiles.
 * Feedback Ti: Varias paradas y poder establecer día y hora para cada una, sería muy muy interesante.
 * Feedback Karley: ícones de clima fossem um pouco menores seria melhor.
 *
 * I can add distance and time with new fields from Direction
 * Should I create some espresso tests for integration?
 *
 * Search history
 *
 * Build route if it was triggered when no connection was available (job schedule?)
 * Agendar uma viagem
 * Notificar quando o tempo mudar apos ter agendado uma viagem
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

    private fun initComponent() {
        setComponent(DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .networkModule(NetworkModule())
                .forecastModule(ForecastModuleAll())
                .build()
        )
    }

    private fun initCoroutines() {
        CoroutineSettings.background = Dispatchers.Default
        CoroutineSettings.io = Dispatchers.IO
        CoroutineSettings.ui = Dispatchers.Main
    }

    private fun initUtil() {
        Util.instance = Android()
    }
}
