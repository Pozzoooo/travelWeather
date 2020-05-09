package pozzo.apps.travelweather.map

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.*
import org.junit.Test
import pozzo.apps.travelweather.core.PermissionChecker

class MapSettingsTest {
    private val permissionChecker: PermissionChecker = mock()
    private val mapSettings = MapSettings(permissionChecker)

    @Test fun assertPermissionDenied() {
        whenever(permissionChecker.isGranted(any())).thenReturn(false)

        assertFalse(mapSettings.isMyLocationEnabled())
    }

    @Test fun assertFullPermission() {
        whenever(permissionChecker.isGranted(any())).thenReturn(true)

        assertTrue(mapSettings.isMyLocationEnabled())
    }
}
