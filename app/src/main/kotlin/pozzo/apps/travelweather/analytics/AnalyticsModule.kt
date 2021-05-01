package pozzo.apps.travelweather.analytics

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.core.injection.AppModule

@Module(includes = [AppModule::class])
open class AnalyticsModule {
    @Provides open fun firebaseAnalytics(application: Application) = FirebaseAnalytics.getInstance(application)
    @Provides open fun mapAnalytics(firebaseAnalytics: FirebaseAnalytics) = MapAnalytics(firebaseAnalytics)
}
