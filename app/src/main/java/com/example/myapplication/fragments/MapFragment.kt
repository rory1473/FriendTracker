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
    //declare class variables
    private val TAG = "MapFragment"
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
        //set bottom navigation as visible
        val bottomNavigationView = activity!!.findViewById(R.id.nav_view) as BottomNavigationView
        bottomNavigationView.visibility = View.VISIBLE
        //configuration for OSMDroid
        Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity))
        //define map view
        val map = fragView.findViewById(R.id.map1) as MapView
        mv = map

        val arg = arguments
        session = arg!!.getString("session")!!

        //call to set map and marker initialise functions
        markerInit()
        mapInit()

        //UserModel class is called with an observer to listen for any changes
        UserModel
        UserModel.addObserver(this)
        PhotoModel
        PhotoModel.addObserver(this)

        //exit button returns user to HomeFragment
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

        //copy button uses the ClipBoard manager to add the session ID to the device clipboard
        val copy = fragView.findViewById(R.id.copy) as Button
        copy.setOnClickListener {
            val clipboard = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("session", session)
            clipboard.primaryClip = clip
            Toast.makeText(activity, "Session Copied to Clipboard", Toast.LENGTH_SHORT).show()
        }
        //button takes you to camera fragment
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
        //set touch controls and default view
        mv.setMultiTouchControls(true)
        val mvController = mv.controller
        mvController.setZoom(16)
        mvController.setCenter(GeoPoint( 50.909698, -1.404351))

    }


    private fun markerInit() {
        //on marker clicked actions
        markerGestureListener = object:OnItemGestureListener<OverlayItem> {

            //on user marker interaction a toast displays the users name
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
            //on single tap on a photo marker the image is displayed in a dialog box
            override fun onItemSingleTapUp(i: Int, item: OverlayItem): Boolean {
                val dialog = Dialog(context!!)
                dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setContentView(R.layout.map_photo)
                dialog.setTitle("Selected Image")
                dialog.setCancelable(true)

                //string is decoded to byte array and then decoded back to a bitmap and set in image view
                val decodedString = Base64.decode(item.snippet, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                val image = dialog.findViewById(R.id.map_imageView) as ImageView
                image.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 200,200, false))

                dialog.show()

                return true
            }
        }


    }


    //Observable function displays and updates markers on map
    override fun update(o: Observable?, arg: Any?) {
        //data is called from the Model classes
        userData = UserModel.getData()!!
        photoData = PhotoModel.getData()!!
        //user marker and photo marker overlay layers are set with gesture listeners
        items = ItemizedIconOverlay<OverlayItem>(activity, ArrayList<OverlayItem>(), markerGestureListener)
        photoItems = ItemizedIconOverlay<OverlayItem>(activity, ArrayList<OverlayItem>(), photoGestureListener)
        //the overlays are clear every the function is called ti prevent trail of markers on map
        mv.overlays.clear()
        Log.i(TAG, "session is " + session)

        for (userData in userData) {
            val curSession = userData.session
            if (curSession == session) {
                Log.i(TAG, userData.name)

                val curName = userData.name
                val curLat = userData.lat.toDouble()
                val curLong = userData.long.toDouble()
                val curColor = userData.color

                val curLocation = OverlayItem(curName, curName, GeoPoint(curLat, curLong))

                //marker colour for each user is set using value from firebase database
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
                //markers are added to overlay and overlay is added to map
                items.addItem(curLocation)
                mv.overlays.add(items)
            }


        }
        for (photoData in photoData) {
            val curSession = photoData.session
            if (curSession == session) {

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