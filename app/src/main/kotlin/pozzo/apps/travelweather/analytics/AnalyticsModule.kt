package pozzo.apps.travelweather.analytics

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import pozzo.apps.travelweather.core.injection.AppModule
import pozzo.apps.travelweather.forecast.ForecastClient
import javax.inject.Singleton

@Module(includes = [AppModule::class])
@InstallIn(ApplicationComponent::class)
open class AnalyticsModule {
    @Provides @Singleton open fun firebaseAnalytics(application: Application) = FirebaseAnalytics.getInstance(application)
}
