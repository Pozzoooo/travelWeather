package pozzo.apps.travelweather.analytics

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import pozzo.apps.travelweather.core.injection.AppModule
import pozzo.apps.travelweather.forecast.ForecastClient

@Module(includes = [AppModule::class])
@InstallIn(ApplicationComponent::class)
open class AnalyticsModule {
    @Provides open fun firebaseAnalytics(application: Application) = FirebaseAnalytics.getInstance(application)
    @Provides open fun mapAnalytics(firebaseAnalytics: FirebaseAnalytics, forecastClient: List<@JvmSuppressWildcards ForecastClient>) =
            MapAnalytics(firebaseAnalytics, forecastClient)
}
