package com.example.myapplication

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private var listener: HomeFragmentListener? = null
    var sessionID = ""
    var name = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       val fragView = inflater.inflate(R.layout.fragment_home, container, false)
        val random = fragView.findViewById<TextView>(R.id.random) as TextView
        val etName = fragView.findViewById<EditText>(R.id.etName) as EditText

        createSessionID(random)
        val btn1 = fragView.findViewById(R.id.btn1) as Button

        btn1.setOnClickListener {
            name = etName.text.toString()
            if (name.isEmpty()){
                etName.error = "Please Enter a Name"
            }else{
                listener?.detailsEntered(name, sessionID)

            }
        }

        return fragView
    }



    private fun createSessionID(random: TextView) {
        val stringLibrary = ('a'..'z').toList().toTypedArray()
        sessionID = (1..6).map { stringLibrary.random() }.joinToString("")

        random.text = sessionID
    }



    interface HomeFragmentListener {
        fun detailsEntered(homeName: String, homeSession: String )

    }




    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement HomeFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }



    companion object {

        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}
