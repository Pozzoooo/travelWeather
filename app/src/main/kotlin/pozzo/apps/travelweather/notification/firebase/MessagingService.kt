package pozzo.apps.travelweather.notification.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pozzo.apps.travelweather.notification.Notifier
import pozzo.apps.travelweather.notification.parser.NotificationParserFactory

class MessagingService : FirebaseMessagingService() {
    private val notificationParserFactory = NotificationParserFactory()
    private val notifier = Notifier()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationHandler = notificationParserFactory.getHandlerFor(remoteMessage)
        val notification = notificationHandler.parse(remoteMessage)
        notifier.notify(this, notification)
    }

    override fun onNewToken(token: String) {
    }
}
