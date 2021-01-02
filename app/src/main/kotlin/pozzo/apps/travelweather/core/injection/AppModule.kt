package pozzo.apps.travelweather.core.injection

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import pozzo.apps.travelweather.core.LastRunRepository
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
open class AppModule
