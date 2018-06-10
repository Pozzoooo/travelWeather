package pozzo.apps.travelweather.map.overlay

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
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
    tutorialPreferences.getBoolean(tutorial.tutorialName, false)

  fun setTutorialPlayed(tutorial: Tutorial) {
    tutorialPreferences.edit().putBoolean(tutorial.tutorialName, true).apply()
  }

  fun playTutorial(activity: Activity) {
    FancyShowCaseView.Builder(activity)
        .focusOn(activity.findViewById(R.id.flagShelf))
        .title(activity.getString(R.string.tutorial_flags))
        .build()
        .show()
  }
}
