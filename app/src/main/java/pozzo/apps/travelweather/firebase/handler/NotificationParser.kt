package pozzo.apps.travelweather.firebase.handler

import com.google.firebase.messaging.RemoteMessage
import pozzo.apps.travelweather.common.notification.NotificationVo

interface NotificationParser {

    fun parse(remoteMessage: RemoteMessage) : NotificationVo
}
