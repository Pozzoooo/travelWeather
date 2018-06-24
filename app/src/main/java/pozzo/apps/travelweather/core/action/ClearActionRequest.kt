package pozzo.apps.travelweather.core.action

import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.map.viewmodel.MapViewModel

class ClearActionRequest(private val mapViewModel: MapViewModel) : ActionRequest(R.string.removeAllMarkers) {
    override fun execute() {
        mapViewModel.setStartPosition(null)
        mapViewModel.setFinishPosition(null)
    }
}
