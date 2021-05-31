package pozzo.apps.travelweather.core

import pozzo.apps.travelweather.R

enum class Warning(val messageId: Int) {
    PERMISSION_DENIED(R.string.warning_permissionDenied),
    CANT_FIND_CURRENT_LOCATION(R.string.warning_currentLocationNotFound),
}
