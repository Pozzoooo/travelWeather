package pozzo.apps.travelweather.core

import pozzo.apps.travelweather.R

enum class Error(val messageId: Int) {
    NO_CONNECTION(R.string.error_needsConnection),
    CANT_REACH(R.string.error_cantReach),
    ADDRESS_NOT_FOUND(R.string.error_addressNotFound),
    CANT_FIND_CURRENT_LOCATION(R.string.error_currentLocationNotFound),
    CANT_FIND_ROUTE(R.string.error_pathNotFound);
}
