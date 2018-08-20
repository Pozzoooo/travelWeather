package pozzo.apps.travelweather.map

import dagger.Component
import pozzo.apps.travelweather.core.injection.AppComponent
import pozzo.apps.travelweather.core.injection.ViewModelScope
import pozzo.apps.travelweather.map.viewmodel.MapViewModel

@ViewModelScope
@Component(dependencies = [AppComponent::class])
interface MapComponent {
    fun inject(mapViewModel: MapViewModel)
}
