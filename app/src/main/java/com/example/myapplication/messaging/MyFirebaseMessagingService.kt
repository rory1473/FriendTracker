package com.example.myapplication.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {






    override fun onMessageReceived(remoteMessage: RemoteMessage) {


        remoteMessage.data.isNotEmpty().let {
            Log.i(" AAAA", remoteMessage.notification!!.title)
            Log.i(" BBBB", remoteMessage.notification!!.body)


            sendNotification(remoteMessage.notification!!.body!!)
        }




//        val notificationBuilder = NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
  //          .setContentTitle(remoteMessage.notification!!.title)
    //        .setContentText(remoteMessage.notification!!.body)
  //          .setPriority(NotificationCompat.PRIORITY_HIGH)
   //         .setStyle(NotificationCompat.BigTextStyle())
   //         .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
   //         .setSmallIcon(R.mipmap.ic_launcher)
  //          .setAutoCancel(true)
//
   //     val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
   //     notificationManager.notify(0, notificationBuilder.build())
    }






    override fun onNewToken(token: String) {
        Log.d("Messaging Service", "Refreshed token: $token")

        sendRegistrationToServer(token)
    }



    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.d("messaging service", "sendRegistrationTokenToServer($token)")
    }


    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.notification_channel_id)
        val channelName = getString(R.string.notification_channel_name)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            //.setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle(getString(R.string.fcm_message))
            .setContentText(messageBody)
            .setSound(defaultSoundUri)
            .setStyle(NotificationCompat.BigTextStyle())
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }


}