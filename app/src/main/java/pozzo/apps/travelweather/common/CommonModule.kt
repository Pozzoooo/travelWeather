package pozzo.apps.travelweather.common

import android.app.Application
import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.analytics.MapAnalytics
import pozzo.apps.travelweather.common.business.PreferencesBusiness
import pozzo.apps.travelweather.core.injection.AppModule

@Module(includes = [AppModule::class])
class CommonModule {

    @Provides fun preferencesBusiness(application: Application, mapAnalytics: MapAnalytics) =
            PreferencesBusiness(application, mapAnalytics)
}
