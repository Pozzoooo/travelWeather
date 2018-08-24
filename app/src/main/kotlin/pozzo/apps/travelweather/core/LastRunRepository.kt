package pozzo.apps.travelweather.core

import android.content.Context
import android.content.SharedPreferences

class LastRunRepository(context: Context) {
    companion object {
        private const val LAST_RUN_PREFERENCES = "lastRunPreferences"
    }

    private val lastRunPreferences : SharedPreferences

    init {
        lastRunPreferences = context.getSharedPreferences(LAST_RUN_PREFERENCES, Context.MODE_PRIVATE)
    }

    fun getLastRun(key: String) = lastRunPreferences.getLong(key, 0L)
    fun hasRun(key: String) = getLastRun(key) != 0L

    fun setRun(key: String) {
        lastRunPreferences.edit().putLong(key, System.currentTimeMillis()).apply()
    }
}
