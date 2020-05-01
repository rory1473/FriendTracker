package com.example.myapplication.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent

import android.content.Intent.getIntent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MyFirebaseMessagingService:  FirebaseMessagingService(), CoroutineScope{

    private lateinit var db: MessageDatabase
    private var session: Session? = null
    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    override fun onMessageReceived(remoteMessage: RemoteMessage) {


        db = MessageDatabase.getDatabase(application)
        //Log.i("GGG ", curSession)
        launch{
            withContext(Dispatchers.IO) {
                session = db.messageDAO().getSessionByID(1)
            }
            val curSession = session!!.curSession
            Log.i(" QQQQQ", curSession)
            var messageID: Long? = null
            val newMessage = Message(user = remoteMessage.notification!!.title!!, message = remoteMessage.notification!!.body!!, session = curSession)
            withContext(Dispatchers.IO) {
                messageID = db.messageDAO().insert(newMessage)
            }
            Log.i("message", newMessage.toString())
        }

            remoteMessage.data.isNotEmpty().let {
                Log.i(" YYYYYY", remoteMessage.notification!!.title)
                Log.i(" ZZZZZZ", remoteMessage.notification!!.body)


            sendNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
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


    private fun sendNotification(messageTitle: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.notification_channel_id)
        val channelName = getString(R.string.notification_channel_name)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            //.setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle(messageTitle)
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