package pozzo.apps.travelweather.core

import android.app.Application
import org.mockito.Mockito
import pozzo.apps.travelweather.analytics.AnalyticsModuleFake
import pozzo.apps.travelweather.common.CommonModule
import pozzo.apps.travelweather.common.CommonModuleFake
import pozzo.apps.travelweather.core.injection.AppModule
import pozzo.apps.travelweather.core.injection.AppModuleFake
import pozzo.apps.travelweather.core.injection.DaggerAppComponent
import pozzo.apps.travelweather.core.injection.NetworkModule
import pozzo.apps.travelweather.direction.DirectionModule
import pozzo.apps.travelweather.direction.DirectionModuleFake
import pozzo.apps.travelweather.forecast.ForecastModuleAll
import pozzo.apps.travelweather.forecast.ForecastModuleFake
import pozzo.apps.travelweather.location.LocationModule
import pozzo.apps.travelweather.location.LocationModuleFake
import pozzo.apps.travelweather.map.MapModule
import pozzo.apps.travelweather.map.MapModuleFake

object TestInjector {

    fun getAppComponent() : DaggerAppComponent.Builder =
            if (TestSettings.IS_INTEGRATION_TEST) getAppComponentIntegration() else getAppComponentFake()

    private fun getAppComponentIntegration() : DaggerAppComponent.Builder {
        val application = Mockito.mock(Application::class.java)
        return DaggerAppComponent.builder()
                .appModule(AppModule(application))
                .forecastModule(ForecastModuleAll())
                .networkModule(NetworkModule())
                .analyticsModule(AnalyticsModuleFake())
                .commonModule(CommonModule())
                .directionModule(DirectionModule())
                .locationModule(LocationModule())
                .mapModule(MapModule())
    }

    private fun getAppComponentFake() : DaggerAppComponent.Builder {
        return DaggerAppComponent.builder()
                .appModule(AppModuleFake())
                .forecastModule(ForecastModuleFake())
                .networkModule(NetworkModule())
                .analyticsModule(AnalyticsModuleFake())
                .commonModule(CommonModuleFake())
                .directionModule(DirectionModuleFake())
                .locationModule(LocationModuleFake())
                .mapModule(MapModuleFake())
    }
}
