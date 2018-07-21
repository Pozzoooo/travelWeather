package pozzo.apps.travelweather.common

import android.app.Application
import android.content.SharedPreferences
import org.mockito.Mockito
import pozzo.apps.travelweather.analytics.MapAnalytics
import pozzo.apps.travelweather.common.business.PreferencesBusiness

class CommonModuleFake : CommonModule() {

    override fun preferencesBusiness(preferences: SharedPreferences, mapAnalytics: MapAnalytics): PreferencesBusiness =
            Mockito.mock(PreferencesBusiness::class.java)

    override fun sharedPreferences(application: Application): SharedPreferences =
            Mockito.mock(SharedPreferences::class.java)
}
