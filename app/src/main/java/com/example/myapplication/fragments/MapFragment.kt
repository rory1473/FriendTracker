package com.example.myapplication.fragments

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
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
import android.view.Window
import android.widget.ImageView
import androidx.core.content.ContextCompat.*
import com.example.myapplication.R
import com.example.myapplication.datamodel.ReadPhoto
import com.example.myapplication.datamodel.ReadUser
import com.example.myapplication.datamodel.UserModel
import com.example.myapplication.datamodel.PhotoModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener
import java.util.*
import kotlin.collections.ArrayList
import android.util.Base64
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView


class MapFragment : Fragment(), Observer {

    private var listener: OnFragmentInteractionListener? = null
    lateinit var items: ItemizedIconOverlay<OverlayItem>
    lateinit var photoItems: ItemizedIconOverlay<OverlayItem>
    lateinit var markerGestureListener: OnItemGestureListener<OverlayItem>
    lateinit var photoGestureListener: OnItemGestureListener<OverlayItem>
    private var userData: ArrayList<ReadUser> = ArrayList()
    private var photoData: ArrayList<ReadPhoto> = ArrayList()
    lateinit var mv: MapView
    var session = ""
    lateinit var drawable: Drawable


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         val fragView = inflater.inflate(R.layout.fragment_map, container, false)

        val bottomNavigationView = activity!!.findViewById(R.id.nav_view) as BottomNavigationView
        bottomNavigationView.visibility = View.VISIBLE

        Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity))

        val map = fragView.findViewById(R.id.map1) as MapView
        mv = map

        val arg = arguments
        session = arg!!.getString("session")!!

        markerInit()
        mapInit()

        UserModel
        UserModel.addObserver(this)
        PhotoModel
        PhotoModel.addObserver(this)

        val exit = fragView.findViewById(R.id.exit) as Button
        exit.setOnClickListener {
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            val fragment = HomeFragment.newInstance()
            transaction.replace(R.id.page_fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        val sessionText = fragView.findViewById(R.id.session) as TextView
        sessionText.text = session
        sessionText.bringToFront()

        val copy = fragView.findViewById(R.id.copy) as Button
        copy.setOnClickListener {
            val clipboard = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("session", session)
            clipboard.primaryClip = clip
            Toast.makeText(activity, "Session Copied to Clipboard", Toast.LENGTH_SHORT).show()
        }


        val cameraBtn = fragView.findViewById(R.id.camera_btn) as FloatingActionButton
        cameraBtn.setOnClickListener {
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            val fragment = CameraFragment.newInstance()
            transaction.replace(R.id.page_fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return fragView
    }

    private fun mapInit(){

        //mv.setBuiltInZoomControls(true)
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

        photoGestureListener = object:OnItemGestureListener<OverlayItem> {

            override fun onItemLongPress(i: Int, item: OverlayItem): Boolean {
                Toast.makeText(activity, item.snippet, Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onItemSingleTapUp(i: Int, item: OverlayItem): Boolean {
                val dialog = Dialog(context!!)
                dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setContentView(R.layout.map_photo)
                dialog.setTitle("Selected Image")
                dialog.setCancelable(true)

                val decodedString = Base64.decode(item.snippet, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                val image = dialog.findViewById(R.id.map_imageView) as ImageView
                image.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 200,200, false))

                dialog.show()

                return true
            }
        }


    }



    override fun update(o: Observable?, arg: Any?) {
        userData = UserModel.getData()!!
        photoData = PhotoModel.getData()!!
        items = ItemizedIconOverlay<OverlayItem>(activity, ArrayList<OverlayItem>(), markerGestureListener)
        photoItems = ItemizedIconOverlay<OverlayItem>(activity, ArrayList<OverlayItem>(), photoGestureListener)
        mv.overlays.clear()
        Log.i("MMMMGGG", "session is " + session)

        for (userData in userData) {
            val curSession = userData.session
            if (curSession == session) {
                Log.i("AAAA", userData.name)

                val curName = userData.name
                val curLat = userData.lat.toDouble()
                val curLong = userData.long.toDouble()
                val curColor = userData.color

                val curLocation = OverlayItem(curName, curName, GeoPoint(curLat, curLong))

                if(curColor == "red") {
                    drawable = getDrawable(context!!, R.drawable.red_marker1)!!
                }
                else if(curColor == "blue") {
                    drawable = getDrawable(context!!, R.drawable.blue_marker1)!!
                }
                else if(curColor == "green") {
                    drawable = getDrawable(context!!, R.drawable.green_marker1)!!
                }
                else if(curColor == "orange") {
                    drawable = getDrawable(context!!, R.drawable.orange_marker1)!!
                }
                else {
                    drawable = getDrawable(context!!, R.drawable.user_marker)!!
                }
                curLocation.setMarker(drawable)
                items.addItem(curLocation)
                mv.overlays.add(items)
            }


        }
        for (photoData in photoData) {
            val curSession = photoData.session
            if (curSession == session) {
                //Log.i("BBBB", photoData.image)

                val curName = photoData.name
                val curImage = photoData.image
                val curLat = photoData.lat.toDouble()
                val curLong = photoData.long.toDouble()

                val mapPhoto = OverlayItem("Image By"+curName, curImage, GeoPoint(curLat, curLong))
                val drawable = getDrawable(context!!, R.drawable.ic_image_black_24dp)
                mapPhoto.setMarker(drawable)
                photoItems.addItem(mapPhoto)
                mv.overlays.add(photoItems)
            }

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