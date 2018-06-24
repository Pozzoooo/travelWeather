package pozzo.apps.travelweather.notification.parser

import com.google.firebase.messaging.RemoteMessage
import com.splunk.mint.Mint
import pozzo.apps.travelweather.notification.model.NotificationVo

class NotificationParserUnknown : NotificationParser {

    override fun parse(remoteMessage: RemoteMessage): NotificationVo {
        Mint.logException(Exception("Unknown notification reached ${remoteMessage.data}"))
        return NotificationVo("")//todo nice random message :)
    }
}
