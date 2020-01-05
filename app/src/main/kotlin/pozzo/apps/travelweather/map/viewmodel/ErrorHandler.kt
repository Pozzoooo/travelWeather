package pozzo.apps.travelweather.map.viewmodel

import pozzo.apps.travelweather.core.Error

interface ErrorHandler {
    fun postError(error: Error)
}
