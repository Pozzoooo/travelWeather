package pozzo.apps.travelweather.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pozzo.apps.travelweather.common.notification.NotificationHandler
import pozzo.apps.travelweather.common.notification.NotificationVo

class MessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            handleMessageWithData(remoteMessage)
        }
    }

    private fun handleMessageWithData(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data

        //todo handle nullable parameters in a more reliable way
        val notification = NotificationVo(data["url"]!!, data["customMessage"]!!)
        NotificationHandler().linkedNotification(this, notification)
    }
}
