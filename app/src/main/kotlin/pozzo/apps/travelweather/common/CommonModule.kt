package pozzo.apps.travelweather.common

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.analytics.MapAnalytics
import pozzo.apps.travelweather.common.business.PreferencesBusiness
import pozzo.apps.travelweather.core.PermissionChecker
import pozzo.apps.travelweather.core.injection.AppModule
import java.util.*

@Module(includes = [AppModule::class])
open class CommonModule {

    @Provides open fun preferencesBusiness(preferences: SharedPreferences, mapAnalytics: MapAnalytics) =
            PreferencesBusiness(preferences, mapAnalytics)

    @Provides open fun sharedPreferences(application: Application) : SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(application)

    @Provides open fun currentDate() : Calendar = Calendar.getInstance()

    @Provides open fun permissionChecker(application: Application) = PermissionChecker(application)
}
