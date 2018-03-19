package pozzo.apps.travelweather.forecast.model

import pozzo.apps.travelweather.R

enum class Day(val resourceId: Int, val forecastIndex: Int) {
    TODAY(R.id.rToday, 0),
    TOMORROW(R.id.rTomorow, 1),
    AFTER_TOMORROW(R.id.rAfterTomorow, 2);

    companion object {
        fun getByResourceId(resourceId: Int): Day =
                Day.values().firstOrNull {
                    it.resourceId == resourceId
                }?.let { it } ?: TODAY
    }
}
