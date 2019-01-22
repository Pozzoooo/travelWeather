package pozzo.apps.travelweather.map.manager

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.core.userinputrequest.LocationPermissionRequest
import pozzo.apps.travelweather.core.userinputrequest.PermissionRequest
import pozzo.apps.travelweather.map.viewmodel.MapViewModel

class PermissionManagerTest {
    @Mock private lateinit var appCompatActivity: AppCompatActivity
    @Mock private lateinit var viewModel: MapViewModel
    @Mock private lateinit var permissionRequester: PermissionRequester

    private lateinit var permissionManager: PermissionManager
    private lateinit var fakePermission: PermissionRequest

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)

        fakePermission = LocationPermissionRequest(mock())
        permissionManager = PermissionManager(appCompatActivity, viewModel, permissionRequester)
    }

    @Test fun shouldRequestPermission() {
        permissionManager.requestPermissions(fakePermission)
        verify(permissionRequester).requestPermissions(appCompatActivity, fakePermission)
    }

    @Test fun shouldCallbackWhenSucceed() {
        permissionManager.requestPermissions(fakePermission)
        val permissionArray = IntArray(1)
        permissionArray.set(0, PackageManager.PERMISSION_GRANTED)
        permissionManager.onRequestPermissionsResult(fakePermission.code(), arrayOf("any"), permissionArray)
        verify(viewModel).onPermissionGranted(fakePermission, appCompatActivity)
    }

    @Test fun shouldCallbackDeniedRequest() {
        permissionManager.requestPermissions(fakePermission)
        val permissionArray = IntArray(1)
        permissionArray.set(0, PackageManager.PERMISSION_DENIED)
        permissionManager.onRequestPermissionsResult(fakePermission.code(), arrayOf("any"), permissionArray)
        verify(viewModel).onPermissionDenied(fakePermission)
    }

    @Test fun shouldLogUnknownResult() {
        val bugInstance = mock<Bug>()
        Bug.setInstance(bugInstance)
        permissionManager.onRequestPermissionsResult(-1, arrayOf("any"), IntArray(0))
        verify(bugInstance).logException(any<String>())
    }
}
