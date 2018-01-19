package work.nityc_nyuta.sirasunakondate

import android.app.Notification
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FireBaseMessaging: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        //通知生成
        val builder = NotificationCompat.Builder(applicationContext)
        builder.setSmallIcon(R.drawable.icon)
        builder.setContentTitle(remoteMessage!!.data.get("title"))
        builder.setContentText(remoteMessage.data.get("body"))
        builder.setDefaults(Notification.DEFAULT_SOUND
                or Notification.DEFAULT_VIBRATE
                or Notification.DEFAULT_LIGHTS)
        builder.color = ContextCompat.getColor(this, R.color.colorDarkBlue)
        builder.setAutoCancel(true)

        val manager = NotificationManagerCompat.from(applicationContext)
        manager.notify(0, builder.build())
    }
}