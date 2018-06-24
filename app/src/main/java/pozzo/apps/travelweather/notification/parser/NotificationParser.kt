package pozzo.apps.travelweather.notification.parser

import com.google.firebase.messaging.RemoteMessage
import pozzo.apps.travelweather.notification.model.NotificationVo

interface NotificationParser {

    fun parse(remoteMessage: RemoteMessage) : NotificationVo
}
