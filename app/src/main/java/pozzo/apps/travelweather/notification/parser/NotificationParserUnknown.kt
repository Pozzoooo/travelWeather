package pozzo.apps.travelweather.notification.parser

import com.google.firebase.messaging.RemoteMessage
import com.splunk.mint.Mint
import pozzo.apps.travelweather.core.bugtracker.Bug
import pozzo.apps.travelweather.notification.model.NotificationVo

class NotificationParserUnknown : NotificationParser {

    override fun parse(remoteMessage: RemoteMessage): NotificationVo {
        Bug.get().logException("Unknown notification reached ${remoteMessage.data}")
        return NotificationVo("")//todo nice random message :)
    }
}
