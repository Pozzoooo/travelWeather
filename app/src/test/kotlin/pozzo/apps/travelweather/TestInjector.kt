package pozzo.apps.travelweather

import android.app.Application
import org.mockito.Mockito
import pozzo.apps.travelweather.analytics.AnalyticsModule
import pozzo.apps.travelweather.common.CommonModule
import pozzo.apps.travelweather.core.injection.AppComponent
import pozzo.apps.travelweather.core.injection.AppModule
import pozzo.apps.travelweather.core.injection.DaggerAppComponent
import pozzo.apps.travelweather.core.injection.NetworkModule
import pozzo.apps.travelweather.direction.DirectionModule
import pozzo.apps.travelweather.forecast.yahoo.ForecastModuleYahoo

object TestInjector {

    fun getAppComponent() : AppComponent {
        val application = Mockito.mock(Application::class.java)
        return DaggerAppComponent.builder()
                .appModule(AppModule(application))
                .forecastModule(ForecastModuleYahoo())
                .networkModule(NetworkModule())
                .analyticsModule(AnalyticsModule())
                .commonModule(CommonModule())
                .directionModule(DirectionModule())
                .build()
    }
}
