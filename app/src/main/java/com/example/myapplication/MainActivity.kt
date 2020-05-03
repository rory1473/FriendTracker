package com.example.myapplication

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.location.LocationManager
import android.location.LocationListener
import android.location.Location
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Base64
import android.widget.Toast
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.datamodel.Photo
import com.example.myapplication.datamodel.User
import com.example.myapplication.fragments.CameraFragment
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener
import com.google.firebase.database.FirebaseDatabase
import com.example.myapplication.fragments.HomeFragment
import com.example.myapplication.fragments.MapFragment
import com.example.myapplication.fragments.MessageFragment
import com.example.myapplication.messaging.Message
import com.example.myapplication.messaging.MessageDatabase
import com.example.myapplication.messaging.MyFirebaseMessagingService
import com.example.myapplication.messaging.Session
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.permission_required.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity(),  HomeFragment.HomeFragmentListener, CameraFragment.CameraFragmentListener, MapFragment.OnFragmentInteractionListener, MessageFragment.OnFragmentInteractionListener {

    private val TAG = "MainActivity"
    private val fm = supportFragmentManager
    var session = ""
    var name = ""
    var color = ""
    private var messages = listOf<Message>()
    private lateinit var db: MessageDatabase
    lateinit var service: LocationService
    lateinit var serviceConn: ServiceConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        val path = File(Environment.getExternalStorageDirectory().toString() + "/skiApp")
        path.mkdirs()

        //val notificationIntent = intent.getStringExtra("messageFragment")
        //if (notificationIntent != null) {
        //    if(notificationIntent == "launch"){
        //        showMessageFragment()
        //} else {
            showHomeFragment()
        //}
    //}

        db = MessageDatabase.getDatabase(application)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                messages = db.messageDAO().getMessages()
            }
            //checks image exists
            if (messages != null) {
                var messageID: Int? = null
                withContext(Dispatchers.IO) {
                    messageID = db.messageDAO().delete(messages)
                }
            }
        }

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
                //Log.d("main", msg)
                //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })
    }



    private fun saveUser(){
        val ref = FirebaseDatabase.getInstance().getReference("user")
        val userID = ref.push().key.toString()
        serviceConn = object: ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                service = ((binder as LocationService.LocationServiceBinder).getService())
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }}

        val startIntent = Intent(this, LocationService::class.java)
        startIntent.putExtra("ID", userID)
        startIntent.putExtra("name", name)
        startIntent.putExtra("session", session)
        startIntent.putExtra("color", color)
        startForegroundService(startIntent)

        val bindIntent = Intent(this, LocationService::class.java)
        bindService(bindIntent, serviceConn,  Context.BIND_AUTO_CREATE)

    }

    override fun detailsEntered(homeName: String, homeSession: String, homeColor: String) {
        name = homeName
        session = homeSession
        color = homeColor
        Log.i("name", name)
        Log.i("session", session)
        Log.i("color", color)
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

        var sessionObject: Session? = null
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                sessionObject = db.messageDAO().getSessionByID(1)
            }
            if (sessionObject != null) {
                sessionObject!!.curSession = session
                sessionObject!!.user = name
                var sessionID: Int? = null
                withContext(Dispatchers.IO) {
                    sessionID = db.messageDAO().updateSession(sessionObject!!)

                }
            }
        }
    }


    override fun photoInterface(newPhoto: ByteArray) {
        val location = service.curLocation
        val lat = location?.latitude.toString()
        val long = location?.longitude.toString()

        val encodedImage = Base64.encodeToString(newPhoto , Base64.DEFAULT)
        val ref = FirebaseDatabase.getInstance().getReference("photo")
        val photoID = ref.push().key.toString()
        val photo = Photo(photoID, name, encodedImage, session, lat, long)
        ref.child(photoID).setValue(photo).addOnCompleteListener{
            Toast.makeText(this, "Photo Uploaded",Toast.LENGTH_LONG).show()
        }
    }


    override fun onFragmentInteraction(uri: Uri) {

    }


    private val onNavigationItemSelectedListener = OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
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
        val args = Bundle()
        args.putString("name", name)
        args.putString("session", session)
        fragment.arguments = args
        transaction.replace(R.id.page_fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }




    override fun onStart() {
        super.onStart()
        getPermissions()
    }

    private fun getPermissions(){
        //check all permissions are granted and request location
        if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED&&
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            //val mgr = getSystemService(Context.LOCATION_SERVICE) as LocationManager
           // mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        }
        else{
            //request permissions
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE ), 0)
        }
    }


    override fun onRequestPermissionsResult(requestCode:Int, permissions:Array<String>, grantResults: IntArray) {
        //if all permissions are granted call getPermissions, otherwise alert user and loop back to getPermissions until user grants permissions
        when(requestCode) {
            0 -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    getPermissions()

                } else {
                    val dialogView = LayoutInflater.from(this).inflate(R.layout.permission_required, null)
                    val builder = AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setTitle("Permissions Needed")
                        .setMessage("You have denied permissions that are required to use this app, to use the app please grant them")
                        .setCancelable(false)
                    val alert = builder.show()
                    dialogView.ok_btn.setOnClickListener {
                        alert.dismiss()
                        getPermissions()
                    }
                }
            }
        }

    }



    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConn)
        val stopIntent = Intent(this, LocationService::class.java)
        stopService(stopIntent)
    }
}