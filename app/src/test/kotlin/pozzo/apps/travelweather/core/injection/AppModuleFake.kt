package pozzo.apps.travelweather.core.injection

import android.app.Application
import org.mockito.Mockito
import pozzo.apps.travelweather.core.LastRunRepository
import pozzo.apps.travelweather.core.PermissionChecker

class AppModuleFake : AppModule(Mockito.mock(Application::class.java)) {
    override fun application() = Mockito.mock(Application::class.java)!!
    override fun lastRunRepository(application: Application) = Mockito.mock(LastRunRepository::class.java)!!
    override fun permissionManager(application: Application) = Mockito.mock(PermissionChecker::class.java)!!
}
