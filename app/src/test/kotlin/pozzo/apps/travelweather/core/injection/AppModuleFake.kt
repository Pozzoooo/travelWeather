package pozzo.apps.travelweather.core.injection

import android.app.Application
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.mockito.Mockito
import pozzo.apps.travelweather.core.LastRunRepository
import pozzo.apps.travelweather.core.PermissionChecker

class AppModuleFake : AppModule(mock { on { getString(com.nhaarman.mockitokotlin2.any()) } doReturn "mockString" }) {
    override fun lastRunRepository(application: Application) = Mockito.mock(LastRunRepository::class.java)!!
    override fun permissionManager(application: Application) = Mockito.mock(PermissionChecker::class.java)!!
}
