package pozzo.apps.travelweather.common

import dagger.Component
import pozzo.apps.travelweather.common.viewmodel.PreferencesViewModel
import pozzo.apps.travelweather.core.injection.AppComponent
import pozzo.apps.travelweather.core.injection.ViewModelScope

@ViewModelScope
@Component(dependencies = [AppComponent::class])
interface CommonComponent {
    fun inject(preferencesViewModel: PreferencesViewModel)
}
