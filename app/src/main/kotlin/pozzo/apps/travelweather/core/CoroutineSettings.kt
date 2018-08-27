package pozzo.apps.travelweather.core

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.android.UI

object CoroutineSettings {
    var background: CoroutineDispatcher = CommonPool
    var ui = UI
}
