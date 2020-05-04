package pozzo.apps.travelweather.core.action

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import pozzo.apps.travelweather.map.viewmodel.MapViewModel

class ClearActionRequestTest {

    @Test fun assertBound() {
        val mapViewModel: MapViewModel = mock()

        ClearActionRequest(mapViewModel).execute()

        verify(mapViewModel).clearStartPosition()
        verify(mapViewModel).clearFinishPosition()
    }
}
