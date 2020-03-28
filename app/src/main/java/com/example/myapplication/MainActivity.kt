package com.example.myapplication

import android.Manifest
import android.net.Uri
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.location.LocationManager
import android.location.LocationListener
import android.location.Location
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener
import com.google.firebase.database.FirebaseDatabase
import android.view.LayoutInflater
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*


class MainActivity : AppCompatActivity(), LocationListener, HomeFragment.HomeFragmentListener, MapFragment.OnFragmentInteractionListener, MessageFragment.OnFragmentInteractionListener {

    private val fm = supportFragmentManager

    var session = ""
    var name = ""
    var lat = 0.0
    var long = 0.0
    var updateID = ""





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        showHomeFragment()

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))


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
        val transaction = fm.beginTransaction()
        val fragment = MapFragment.newInstance()
        transaction.replace(R.id.page_fragment, fragment)
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
            mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this);
        }
        else{
            requestPermissions(arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE ), 0)
        }
    }

    override fun onLocationChanged(newLoc: Location) {

        Log.i("latitude", newLoc.latitude.toString())
        Log.i("longitude", newLoc.longitude.toString())

        lat = newLoc.latitude
        long = newLoc.longitude

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