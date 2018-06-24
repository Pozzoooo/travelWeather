package pozzo.apps.travelweather.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.splunk.mint.Mint
import pozzo.apps.travelweather.common.notification.NotificationHandler
import pozzo.apps.travelweather.common.notification.NotificationVo

//todo how much should I split this class?
class MessagingService : FirebaseMessagingService() {
    companion object {
        private const val URL = "url"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            handleMessageWithData(remoteMessage)
        }
    }

    private fun handleMessageWithData(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data

        if (data.containsKey(URL)) {
            handleLinkedNotification(remoteMessage)
        }
    }

    private fun handleLinkedNotification(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val url = data[URL]
        val customMessage = data["customMessage"]

        if (url != null && customMessage != null) {
            val notification = NotificationVo(url, customMessage)
            NotificationHandler().linkedNotification(this, notification)
        } else {
            Mint.logException(Exception("Missing pieces: $url and $customMessage"))
        }
    }
}
