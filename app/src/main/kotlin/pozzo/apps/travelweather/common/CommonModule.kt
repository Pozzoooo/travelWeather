package pozzo.apps.travelweather.common

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import pozzo.apps.travelweather.core.injection.AppModule
import java.util.*

@Module(includes = [AppModule::class])
@InstallIn(ApplicationComponent::class)
open class CommonModule {

    @Provides open fun sharedPreferences(application: Application) : SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(application)

    @Provides open fun currentDate() : Calendar = Calendar.getInstance()
}
