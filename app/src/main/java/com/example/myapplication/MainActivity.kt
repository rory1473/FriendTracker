package com.example.myapplication

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.net.Uri
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.location.LocationManager
import android.location.LocationListener
import android.location.Location
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import android.util.Log
import com.example.myapplication.datamodel.User
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener
import com.google.firebase.database.FirebaseDatabase
import com.example.myapplication.fragments.HomeFragment
import com.example.myapplication.fragments.MapFragment
import com.example.myapplication.fragments.MessageFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity(), LocationListener, HomeFragment.HomeFragmentListener, MapFragment.OnFragmentInteractionListener, MessageFragment.OnFragmentInteractionListener {

    private val TAG = "MainActivity"
    private val fm = supportFragmentManager
    var session = ""
    var name = ""
    var lat = ""
    var long = ""
    var updateID = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        showHomeFragment()

        val channelId = getString(R.string.notification_channel_id)
        val channelName = getString(R.string.notification_channel_name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
                channel.enableLights(true)
                channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }


        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("main", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                val msg = getString(R.string.msg_token, token).toString()
                Log.d("main", msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })




    }





    private fun saveUser(){
        val ref = FirebaseDatabase.getInstance().getReference("user")
        val userID = ref.push().key.toString()
        val user = User(userID, name, session, lat, long)
        ref.child(userID).setValue(user).addOnCompleteListener{
            Toast.makeText(this, "User Session Started",Toast.LENGTH_LONG).show()
        }
        updateID = userID
    }

    override fun detailsEntered(homeName: String, homeSession: String){
        name = homeName
        session = homeSession
        Log.i("name", name)
        Log.i("session", session)
        saveUser()

        val mapFragment = MapFragment()
        val args = Bundle()
        args.putString("session", session)
        mapFragment.arguments = args
        val transaction = fm.beginTransaction()
        transaction.replace(R.id.page_fragment, mapFragment)
        transaction.addToBackStack(null)
        transaction.commit()

        FirebaseMessaging.getInstance().subscribeToTopic(session)
            .addOnCompleteListener { task ->
                var msg = "connected to group"
                if (!task.isSuccessful) {
                    msg = "failed to connect to group"
                }
                Log.d(TAG, msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
    }



    override fun onFragmentInteraction(uri: Uri) {

    }

    private val onNavigationItemSelectedListener = OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.navigation_home -> {
                showHomeFragment()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_map -> {
                showMapFragment()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_messages -> {
                showMessageFragment()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }



    private fun showHomeFragment() {
        val transaction = fm.beginTransaction()
        val fragment = HomeFragment.newInstance()
        transaction.replace(R.id.page_fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    private fun showMapFragment() {
        val mapFragment = MapFragment()
        val args = Bundle()
        args.putString("session", session)
        mapFragment.arguments = args
        val transaction = fm.beginTransaction()
        transaction.replace(R.id.page_fragment, mapFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun showMessageFragment() {
        val transaction = fm.beginTransaction()
        val fragment = MessageFragment.newInstance()
        transaction.replace(R.id.page_fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }



    override fun onStart() {
        super.onStart()
        getLocation()
    }


    private fun getLocation(){

        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            val mgr = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        }
        else{
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE ), 0)
        }
    }

    override fun onLocationChanged(newLoc: Location) {

        //Log.i("latitude", newLoc.latitude.toString())
        //Log.i("longitude", newLoc.longitude.toString())

        val lat = newLoc.latitude.toString()
        val long = newLoc.longitude.toString()

        if (updateID != "") {
            val ref = FirebaseDatabase.getInstance().getReference("user")
            val user = User(updateID, name, session, lat, long)
            ref.child(updateID).setValue(user)
        }
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

    override fun onRequestPermissionsResult(requestCode:Int, permissions:Array<String>, grantResults: IntArray) {
        when(requestCode) {
            0 -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()

                } else {
                    Toast.makeText(this, "Location, file read and file write permissions required to use this app", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}