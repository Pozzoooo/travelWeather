package pozzo.apps.travelweather.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object CoroutineSettings {
    var background: CoroutineDispatcher = Dispatchers.Unconfined
    var io: CoroutineDispatcher = Dispatchers.Unconfined
    var ui: CoroutineDispatcher = Dispatchers.Unconfined
}
