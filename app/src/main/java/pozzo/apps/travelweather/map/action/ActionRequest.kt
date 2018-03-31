package pozzo.apps.travelweather.map.action

/**
 * Ideally I would have a dependency inversion here, though I will cut off some verbosity for now.
 */
abstract class ActionRequest(val messageId: Int) {
    abstract fun execute()
}
