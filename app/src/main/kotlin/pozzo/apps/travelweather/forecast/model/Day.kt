package pozzo.apps.travelweather.forecast.model

enum class Day(val index: Int) {
    TODAY(0),
    TOMORROW(1),
    AFTER_TOMORROW(2);

    companion object {
        fun getByIndex(index: Int): Day =
                Day.values().firstOrNull {
                    it.index == index
                }?.let { it } ?: TODAY
    }
}
