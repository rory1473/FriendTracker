package com.example.myapplication

import android.content.Context
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_map.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
import android.location.LocationManager
import android.location.LocationListener
import android.content.pm.PackageManager
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*


class MapFragment : Fragment(), LocationListener {
    lateinit var items: ItemizedIconOverlay<OverlayItem>
    private var listener: OnFragmentInteractionListener? = null
    lateinit var mv: MapView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         val fragView = inflater.inflate(R.layout.fragment_map, container, false)

        Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity))

        val map = fragView.findViewById(R.id.map1) as MapView

        mv = map

        mapInit()
        return fragView
    }

    // TODO: Rename method, update argument and hook method into UI event
    //fun onButtonPressed(uri: Uri) {
    //    listener?.onFragmentInteraction(uri)
    //}

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
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MapFragment()
    }

    private fun mapInit(){

        mv.setBuiltInZoomControls(true)
        mv.setMultiTouchControls(true)

        val mvController = mv.controller
        mvController.setZoom(16)
        mvController.setCenter(GeoPoint( 50.909698, -1.404351))

    }



    override fun onLocationChanged(newLoc: Location) {

        items = ItemizedIconOverlay<OverlayItem>(activity, arrayListOf<OverlayItem>(),null)
        val mvController = mv.controller
        mvController.setZoom(16)
        mvController.setCenter(GeoPoint(newLoc.latitude, newLoc.longitude))
    }

    override fun onProviderDisabled(provider: String) {
        Toast.makeText(
            activity, "Provider " + provider +
                    " disabled", Toast.LENGTH_LONG
        ).show()
    }

    override fun onProviderEnabled(provider: String) {
        Toast.makeText(
            activity, "Provider " + provider +
                    " enabled", Toast.LENGTH_LONG
        ).show()
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

        Toast.makeText(
            activity, "Status changed: $status",
            Toast.LENGTH_LONG
        ).show()
    }
}
