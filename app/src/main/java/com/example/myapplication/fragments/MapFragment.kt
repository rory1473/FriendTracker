package com.example.myapplication.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
import android.util.Log
import androidx.core.content.ContextCompat.*
import com.example.myapplication.R
import com.example.myapplication.datamodel.ReadUser
import com.example.myapplication.datamodel.UserModel
import com.google.firebase.database.*
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener
import java.util.*
import kotlin.collections.ArrayList


class MapFragment : Fragment(), Observer {

    private var listener: OnFragmentInteractionListener? = null
    lateinit var items: ItemizedIconOverlay<OverlayItem>
    lateinit var markerGestureListener: OnItemGestureListener<OverlayItem>
    var data: ArrayList<ReadUser> = ArrayList()
    lateinit var mv: MapView

    val ref = FirebaseDatabase.getInstance().getReference("user")
    var session = ""
    var lat = 50.909698
    var long = -1.404351
    var curLocation = OverlayItem("User Location", "You are Here", GeoPoint(lat, long))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         val fragView = inflater.inflate(R.layout.fragment_map, container, false)

        Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity))

        val map = fragView.findViewById(R.id.map1) as MapView
        mv = map

        markerInit()
        mapInit()

        UserModel
        UserModel.addObserver(this)

        return fragView
    }


    fun updateSession(newSession: String){
        session = newSession
    }


    private fun mapInit(){

        mv.setBuiltInZoomControls(true)
        mv.setMultiTouchControls(true)

        val mvController = mv.controller
        mvController.setZoom(16)
        mvController.setCenter(GeoPoint( 50.909698, -1.404351))

    }


    private fun markerInit() {
        markerGestureListener = object:OnItemGestureListener<OverlayItem> {

            override fun onItemLongPress(i: Int, item: OverlayItem): Boolean {
                Toast.makeText(activity, item.snippet, Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onItemSingleTapUp(i: Int, item: OverlayItem): Boolean {
                Toast.makeText(activity, item.snippet, Toast.LENGTH_SHORT).show()
                return true
            }
        }
        items = ItemizedIconOverlay<OverlayItem>(activity, ArrayList<OverlayItem>(), markerGestureListener)

        val drawable = getDrawable(context!!, R.drawable.user_marker)
        curLocation.setMarker(drawable)
        items.addItem(curLocation)
        mv.overlays.add(items)
    }



    override fun update(o: Observable?, arg: Any?) {
        data = UserModel.getData()!!

            for (userData in data) {
                Log.i("AAAA", userData.name)

                val curName = userData.name
                val curLat = userData.lat.toDouble()
                val curLong = userData.long.toDouble()

                val curLocation = OverlayItem(curName, "You are Here", GeoPoint(curLat, curLong))
                val drawable = getDrawable(context!!, R.drawable.user_marker)
                curLocation.setMarker(drawable)
                items.addItem(curLocation)
                mv.overlays.add(items)


            }

    }





























override fun onAttach(context: Context) {
    super.onAttach(context)
    if (context is OnFragmentInteractionListener) {
        listener = context
    } else {
        throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
    }
}

override fun onDetach() {
    super.onDetach()
    listener = null
}


interface OnFragmentInteractionListener {

    fun onFragmentInteraction(uri: Uri)
}

companion object {
    @JvmStatic
    fun newInstance() = MapFragment()
}
}