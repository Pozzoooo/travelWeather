package pozzo.apps.travelweather.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pozzo.apps.travelweather.common.notification.Notifier
import pozzo.apps.travelweather.firebase.handler.NotificationParserFactory

class MessagingService : FirebaseMessagingService() {
    private val notificationParserFactory = NotificationParserFactory()
    private val notifier = Notifier()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationHandler = notificationParserFactory.getHandlerFor(remoteMessage)
        val notification = notificationHandler.parse(remoteMessage)
        notifier.notify(this, notification)
    }
}
