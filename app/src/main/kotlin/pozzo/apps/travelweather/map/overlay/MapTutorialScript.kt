package pozzo.apps.travelweather.map.overlay

import pozzo.apps.travelweather.core.LastRunRepository

class MapTutorialScript(private val lastRunRepository: LastRunRepository) {
    lateinit var playTutorialCallback: (tutorial: LastRunKey) -> Unit

    fun onAppStart() {
        playIfNotPlayed(LastRunKey.FULL_TUTORIAL)
    }

    fun onFinishPositionSet() {
        playIfNotPlayed(LastRunKey.ROUTE_CREATED_TUTORIAL)
    }

    private fun playIfNotPlayed(tutorial: LastRunKey) {
        if (!hasPlayed(tutorial)) {
            playTutorialCallback(tutorial)
            setTutorialPlayed(tutorial)
        }
    }

    fun hasPlayed(tutorial: LastRunKey) = lastRunRepository.hasRun(tutorial.key)

    private fun setTutorialPlayed(tutorial: LastRunKey) {
        lastRunRepository.setRun(tutorial.key)
    }
}
