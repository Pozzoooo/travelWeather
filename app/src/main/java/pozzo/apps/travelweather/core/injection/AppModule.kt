package pozzo.apps.travelweather.core.injection

import android.app.Application
import dagger.Module
import dagger.Provides
import pozzo.apps.travelweather.core.PermissionChecker
import javax.inject.Singleton

@Module
class AppModule(private val application: Application) {

    @Provides
    @Singleton
    fun application(): Application = application

    @Provides fun permissionManager(application: Application): PermissionChecker = PermissionChecker(application)
}
