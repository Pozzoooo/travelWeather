package pozzo.apps.travelweather.map

import dagger.Component
import pozzo.apps.travelweather.core.injection.ActivityScope
import pozzo.apps.travelweather.core.injection.AppComponent
import pozzo.apps.travelweather.map.viewmodel.MapViewModel

@ActivityScope
@Component(dependencies = [AppComponent::class])
interface MapComponent {
    fun inject(mapViewModel: MapViewModel)
}
