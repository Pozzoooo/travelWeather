package pozzo.apps.travelweather.notification.model

import android.app.PendingIntent
import android.content.Context

open class NotificationVo(val message: String) {

    open fun getPendingIntent(context: Context) : PendingIntent? = null
}
