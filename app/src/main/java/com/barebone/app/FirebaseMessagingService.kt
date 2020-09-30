package com.barebone.app

import android.R
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FirebaseMessagingServce"


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        var notificationTitle: String? = null
        var notificationBody: String? = null

        // Check if message contains a notification payload
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body)
            notificationTitle = remoteMessage.notification!!.title
            notificationBody = remoteMessage.notification!!.body
        }

        // If you want to fire a local notification (that notification on the top of the phone screen)
        // you should fire it from here

        sendLocalNotification(notificationTitle, notificationBody)
    }

    private fun sendLocalNotification(notificationTitle: String?, notificationBody: String?) {
        val intent = Intent(this, BottomNav::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.alert_dark_frame) //Notification icon
            .setContentIntent(pendingIntent)
            .setContentTitle(notificationTitle)
            .setContentText(notificationBody)
            .setSound(defaultSoundUri)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager!!.notify(1234, notificationBuilder.build())
    }
}