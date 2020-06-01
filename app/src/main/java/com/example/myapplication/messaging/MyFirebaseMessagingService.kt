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
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MyFirebaseMessagingService:  FirebaseMessagingService(), CoroutineScope{
    //declare class variables
    private val TAG = "MessagingService"
    private lateinit var db: MessageDatabase
    private var session: Session? = null
    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        db = MessageDatabase.getDatabase(application)
        //get session from database
        launch {
            withContext(Dispatchers.IO) {
                session = db.messageDAO().getSessionByID(1)
            }
            val curSession = session!!.curSession
            Log.i(TAG+" Session", curSession)

            //add new message to database
            var messageID: Long? = null
            val newMessage = Message(
                user = remoteMessage.notification!!.title!!,
                message = remoteMessage.notification!!.body!!,
                session = curSession
            )
            withContext(Dispatchers.IO) {
                messageID = db.messageDAO().insert(newMessage)
            }
            Log.i(TAG+" message", newMessage.toString())
            Log.i(TAG+" messageID", messageID.toString())
            val name = session!!.user

            remoteMessage.data.isNotEmpty().let {
                Log.i(TAG+" title", remoteMessage.notification!!.title)
                Log.i(TAG+" body", remoteMessage.notification!!.body)
                //if message received from another user then pass values to sendNotification
                if (remoteMessage.notification!!.title!! != name){
                    sendNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
                }
            }

        }

    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

    private fun sendNotification(messageTitle: String, messageBody: String) {
        //set pending intent to launch message fragment
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("messageFragment", "launch")
        val pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_ONE_SHOT)

        //build message notification
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
        //check system version compatibility and create channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        //display notification
        notificationManager.notify(0 , notificationBuilder.build())
    }


}