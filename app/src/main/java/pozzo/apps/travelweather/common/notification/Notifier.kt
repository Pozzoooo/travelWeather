package pozzo.apps.travelweather.common.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import pozzo.apps.travelweather.R

class Notifier {

    fun notify(context: Context, notificationVo: NotificationVo) {
        val notificationBuilder =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = context.getString(R.string.notificationChannel_linked)
            createNotificationChannel(context, channel)
            NotificationCompat.Builder(context, channel)
        } else {
            NotificationCompat.Builder(context)
        }

        val notification = notificationBuilder
                .setContentIntent(notificationVo.getPendingIntent(context))
                .setContentText(notificationVo.message)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_drawer)//todo need to find a proper notification icon
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

        NotificationManagerCompat.from(context).notify(0, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context, channelId: String) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelId, importance)
        channel.description = channelId
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}
