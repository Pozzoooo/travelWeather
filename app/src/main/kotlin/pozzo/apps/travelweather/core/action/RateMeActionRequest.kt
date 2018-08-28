package pozzo.apps.travelweather.core.action

import android.content.Context
import pozzo.apps.tools.AndroidUtil
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.analytics.MapAnalytics
import pozzo.apps.travelweather.core.LastRunRepository
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.map.overlay.MapTutorialScript
import pozzo.apps.travelweather.map.overlay.LastRunKey

class RateMeActionRequest(private val context: Context, private val mapAnalytics: MapAnalytics)
    : ActionRequest(R.string.rateMe) {

    companion object {
        const val AMOUNT_OF_OCCURRENCES = 2
    }

    override fun execute() {
        mapAnalytics.sendIWantToRate()
        if (!AndroidUtil.openUrl(context.getString(R.string.googlePlay), context)) {
            Bug.get().logException("Hmm, seems like we have an Android that can't display google play links...")
        }
    }

    fun isTimeToDisplay(mapTutorialScript: MapTutorialScript, lastRunRepository: LastRunRepository, daySelectionCount: Int) : Boolean {
        return !lastRunRepository.hasRun(LastRunKey.RATE_DIALOG.key)
                && daySelectionCount > RateMeActionRequest.AMOUNT_OF_OCCURRENCES
                && mapTutorialScript.hasPlayed(LastRunKey.ROUTE_CREATED_TUTORIAL)
    }
}