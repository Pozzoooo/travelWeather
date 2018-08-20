package pozzo.apps.travelweather.notification.model

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri

class NotificationLinked(val link: String, message: String) : NotificationVo(message) {

    override fun getPendingIntent(context: Context): PendingIntent? {
        //todo need to assert intent availability
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        return PendingIntent.getActivity(context, 0, intent, 0)
    }
}
