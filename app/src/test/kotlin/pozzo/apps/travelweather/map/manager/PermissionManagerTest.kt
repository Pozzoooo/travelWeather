package pozzo.apps.travelweather.map.manager

import androidx.appcompat.app.AppCompatActivity
import org.junit.Before
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import pozzo.apps.travelweather.map.viewmodel.MapViewModel

class PermissionManagerTest {
    @Mock private lateinit var appCompatActivity: AppCompatActivity
    @Mock private lateinit var viewModel: MapViewModel

    private lateinit var permissionManager: PermissionManager

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)

        permissionManager = PermissionManager(appCompatActivity, viewModel)
    }
}
