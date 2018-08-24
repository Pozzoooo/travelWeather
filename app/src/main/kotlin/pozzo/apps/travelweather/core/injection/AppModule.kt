package pozzo.apps.travelweather.core.injection

import android.app.Application
import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.core.LastRunRepository
import pozzo.apps.travelweather.core.PermissionChecker
import javax.inject.Singleton

@Module
open class AppModule(private val application: Application) {

    @Provides @Singleton open fun application() = application
    @Provides @Singleton open fun lastRunRepository(application: Application) = LastRunRepository(application)
    @Provides open fun permissionManager(application: Application) = PermissionChecker(application)
}
