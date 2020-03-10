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
import kotlinx.android.synthetic.main.activity_main.*


class MapFragment : Fragment(), LocationListener {

    val mv = map1 as MapView
    val mvController = mv.controller
    lateinit var items: ItemizedIconOverlay<OverlayItem>

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        mapInit()
        return inflater.inflate(R.layout.fragment_map, container, false)
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = MapFragment()
    }

    private fun mapInit(){

        mv.setBuiltInZoomControls(true)
        mv.setMultiTouchControls(true)

        mvController.setZoom(16)
        mvController.setCenter(GeoPoint( 50.909698, -1.404351))

    }



    override fun onLocationChanged(newLoc: Location) {

        items = ItemizedIconOverlay<OverlayItem>(getContext(), arrayListOf<OverlayItem>(),null)
        mvController.setZoom(16)
        mvController.setCenter(GeoPoint(newLoc.latitude, newLoc.longitude));
    }

    override fun onProviderDisabled(provider: String) {
        Toast.makeText(
            getActivity(), "Provider " + provider +
                    " disabled", Toast.LENGTH_LONG
        ).show()
    }

    override fun onProviderEnabled(provider: String) {
        Toast.makeText(
            getActivity(), "Provider " + provider +
                    " enabled", Toast.LENGTH_LONG
        ).show()
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

        Toast.makeText(
            getActivity(), "Status changed: $status",
            Toast.LENGTH_LONG
        ).show()
    }
}
