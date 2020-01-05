package pozzo.apps.travelweather.common

import android.app.Application
import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.mock
import org.mockito.Mockito
import pozzo.apps.travelweather.analytics.MapAnalytics
import pozzo.apps.travelweather.common.business.PreferencesBusiness
import pozzo.apps.travelweather.core.PermissionChecker
import java.util.*

class CommonModuleFake : CommonModule() {

    override fun preferencesBusiness(preferences: SharedPreferences, mapAnalytics: MapAnalytics): PreferencesBusiness =
            Mockito.mock(PreferencesBusiness::class.java)

    override fun sharedPreferences(application: Application): SharedPreferences =
            Mockito.mock(SharedPreferences::class.java)

    override fun currentDate() : Calendar =
            Calendar.getInstance().apply { set(1990, 10, 24, 0, 45) }

    override fun permissionChecker(application: Application): PermissionChecker = mock()
}
