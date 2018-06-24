package pozzo.apps.travelweather.notification.parser

import com.google.firebase.messaging.RemoteMessage

class NotificationParserFactory {
    companion object {
        const val URL = "url"
    }

    fun getHandlerFor(remoteMessage: RemoteMessage) : NotificationParser = when {
        remoteMessage.data.isNotEmpty() -> handlerWithData(remoteMessage)
        else -> NotificationParserUnknown()
    }

    private fun handlerWithData(remoteMessage: RemoteMessage) : NotificationParser {
        val data = remoteMessage.data ?: return NotificationParserUnknown()

        return when {
            data.containsKey(URL) -> NotificationParserLinked()
            else -> NotificationParserUnknown()
        }
    }
}
