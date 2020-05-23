package pozzo.apps.travelweather.common

import android.app.Application
import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.mockito.Mockito.mock
import pozzo.apps.travelweather.analytics.MapAnalytics
import pozzo.apps.travelweather.common.business.PreferencesBusiness
import pozzo.apps.travelweather.core.PermissionChecker
import pozzo.apps.travelweather.forecast.model.Day
import java.util.*

class CommonModuleFake : CommonModule() {

    override fun preferencesBusiness(preferences: SharedPreferences, mapAnalytics: MapAnalytics): PreferencesBusiness =
            mock {
                on { getSelectedDay() } doReturn Day.TOMORROW
            }

    override fun sharedPreferences(application: Application): SharedPreferences =
            mock(SharedPreferences::class.java)

    override fun currentDate(): Calendar =
            Calendar.getInstance().apply { set(1990, 10, 24, 0, 45) }

    override fun permissionChecker(application: Application): PermissionChecker = mock()
}
