package com.example.myapplication

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.util.Log
import com.example.myapplication.datamodel.User
import com.google.firebase.database.FirebaseDatabase


class LocationService : Service(), LocationListener {
    var curLocation: Location? = null
    var channelID = "location_channel"
    var userID = ""
    var name = ""
    var session = ""

        private set
    override fun onStartCommand(intent: Intent?, startFlags: Int, id: Int): Int {
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            val mgr = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        }

        userID = intent!!.getStringExtra("ID")
        name = intent.getStringExtra("name")
        session = intent.getStringExtra("session")



        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification = NotificationCompat.Builder(this, channelID)
            .setContentTitle("Location Service")
            .setSmallIcon(R.drawable.user_marker)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)


        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(channelID, "Location Service Channel", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }


    inner class LocationServiceBinder : Binder()
    {
        fun getService() : LocationService
        {
            return this@LocationService
        }
    }

    override fun onBind(intent: Intent): IBinder {

        return LocationServiceBinder()
    }


    override fun onLocationChanged(newLoc: Location) {
        curLocation = newLoc

        val lat = newLoc.latitude.toString()
        val long = newLoc.longitude.toString()

        if (userID != "") {
            val ref = FirebaseDatabase.getInstance().getReference("user")
            val user = User(userID, name, session, lat, long)
            ref.child(userID).setValue(user)
        }
        Log.i("lat", lat)
        Log.i("long", long)
    }

    override fun onProviderDisabled(provider: String) {
        Toast.makeText(
            this, "Provider " + provider +
                    " disabled", Toast.LENGTH_LONG
        ).show()
    }

    override fun onProviderEnabled(provider: String) {
        Toast.makeText(
            this, "Provider " + provider +
                    " enabled", Toast.LENGTH_LONG
        ).show()
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

        Toast.makeText(
            this, "Status changed: $status",
            Toast.LENGTH_LONG
        ).show()
    }

}