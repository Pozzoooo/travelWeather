package pozzo.apps.travelweather.analytics

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import org.mockito.Mockito

class AnalyticsModuleFake : AnalyticsModule() {

    override fun firebaseAnalytics(application: Application): FirebaseAnalytics =
            Mockito.mock(FirebaseAnalytics::class.java)

    override fun mapAnalytics(firebaseAnalytics: FirebaseAnalytics): MapAnalytics =
            Mockito.mock(MapAnalytics::class.java)
}
