package pozzo.apps.travelweather.analytics

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.core.injection.AppModule

@Module(includes = [AppModule::class])
class AnalyticsModule {

    @Provides fun firebaseAnalytics(application: Application) = FirebaseAnalytics.getInstance(application)

    @Provides fun mapAnalytics(firebaseAnalytics: FirebaseAnalytics) = MapAnalytics(firebaseAnalytics)
}
