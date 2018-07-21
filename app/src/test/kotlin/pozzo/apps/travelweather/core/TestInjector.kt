package pozzo.apps.travelweather.core

import android.app.Application
import org.mockito.Mockito
import pozzo.apps.travelweather.analytics.AnalyticsModule
import pozzo.apps.travelweather.analytics.AnalyticsModuleFake
import pozzo.apps.travelweather.common.CommonModule
import pozzo.apps.travelweather.common.CommonModuleFake
import pozzo.apps.travelweather.core.injection.AppModule
import pozzo.apps.travelweather.core.injection.DaggerAppComponent
import pozzo.apps.travelweather.core.injection.NetworkModule
import pozzo.apps.travelweather.direction.DirectionModule
import pozzo.apps.travelweather.forecast.ForecastModuleFake
import pozzo.apps.travelweather.forecast.yahoo.ForecastModuleYahoo

object TestInjector {

    fun getAppComponent() : DaggerAppComponent.Builder =
            if (TestSettings.IS_INTEGRATION_TEST) getAppComponentIntegration() else getAppComponentFake()

    private fun getAppComponentIntegration() : DaggerAppComponent.Builder {
        val application = Mockito.mock(Application::class.java)
        return DaggerAppComponent.builder()
                .appModule(AppModule(application))
                .forecastModule(ForecastModuleYahoo())
                .networkModule(NetworkModule())
                .analyticsModule(AnalyticsModule())
                .commonModule(CommonModule())
                .directionModule(DirectionModule())
    }

    private fun getAppComponentFake() : DaggerAppComponent.Builder {
        val application = Mockito.mock(Application::class.java)
        return DaggerAppComponent.builder()
                .appModule(AppModule(application))
                .forecastModule(ForecastModuleFake())
                .networkModule(NetworkModule())
                .analyticsModule(AnalyticsModuleFake())
                .commonModule(CommonModuleFake())
                .directionModule(DirectionModule())
    }
}
