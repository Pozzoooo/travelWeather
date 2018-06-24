package pozzo.apps.travelweather.firebase.handler

import com.google.firebase.messaging.RemoteMessage
import pozzo.apps.travelweather.common.notification.NotificationLinked
import pozzo.apps.travelweather.common.notification.NotificationVo

class NotificationParserLinked : NotificationParser {

    override fun parse(remoteMessage: RemoteMessage): NotificationVo {
        val data = remoteMessage.data
        val url = data[NotificationParserFactory.URL]
        val customMessage = data["customMessage"]

        return if (url != null && customMessage != null) {
            NotificationLinked(url, customMessage)
        } else {
            NotificationParserUnknown().parse(remoteMessage)
        }
    }
}
