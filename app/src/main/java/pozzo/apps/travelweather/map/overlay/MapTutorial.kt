package pozzo.apps.travelweather.map.overlay

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.Gravity
import me.toptas.fancyshowcase.FancyShowCaseView
import pozzo.apps.travelweather.R

class MapTutorial(context: Context) {
    companion object {
        private const val TUTORIAL_PREFERENCES = "tutorialPreferences"
    }

    private val tutorialPreferences : SharedPreferences

    init {
        tutorialPreferences = context.getSharedPreferences(TUTORIAL_PREFERENCES, Context.MODE_PRIVATE)
    }

    fun hasPlayed(tutorial: Tutorial) =
            tutorialPreferences.getLong(tutorial.tutorialName, 0L) != 0L

    fun setTutorialPlayed(tutorial: Tutorial) {
        tutorialPreferences.edit().putLong(tutorial.tutorialName, System.currentTimeMillis()).apply()
    }

    fun playTutorial(activity: Activity) {
        FancyShowCaseView.Builder(activity)
                .focusOn(activity.findViewById(R.id.startFlag))
                .title(activity.getString(R.string.tutorial_flags))
                .titleGravity(Gravity.TOP)
                .build()
                .show()
    }

    fun playRouteCreatedTutorial(activity: Activity) {
        FancyShowCaseView.Builder(activity)
                .title(activity.getString(R.string.tutorial_longPressToDrag))
                .build()
                .show()
    }
}
