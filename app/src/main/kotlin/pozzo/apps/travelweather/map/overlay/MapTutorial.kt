package pozzo.apps.travelweather.map.overlay

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.Gravity
import me.toptas.fancyshowcase.FancyShowCaseView
import pozzo.apps.travelweather.R

class MapTutorial {

    fun playDragTheFlag(activity: Activity) {
        FancyShowCaseView.Builder(activity)
                .focusOn(activity.findViewById(R.id.startFlag))
                .title(activity.getString(R.string.tutorial_flags))
                .titleGravity(Gravity.TOP)
                .build()
                .show()
    }

    fun playDragAgain(activity: Activity) {
        FancyShowCaseView.Builder(activity)
                .title(activity.getString(R.string.tutorial_longPressToDrag))
                .build()
                .show()
    }

    fun playDaySelectionTutorial(activity: Activity) {
        FancyShowCaseView.Builder(activity)
                .title(activity.getString(R.string.tutorial_daySelection))
                .focusOn(activity.findViewById(R.id.spinnerDaySelection))
                .build()
                .show()
    }

    fun playOpenForecastDetails(activity: Activity) {
        FancyShowCaseView.Builder(activity)
                .title(activity.getString(R.string.tutorial_openForecastDetails))
                .build()
                .show()
    }
}
