package pozzo.apps.travelweather.map.movement

class Direction {

    var value = .0
        set(value) {
            if (value >= .0) field = value
        }

    fun hasValue() = value != .0
}
