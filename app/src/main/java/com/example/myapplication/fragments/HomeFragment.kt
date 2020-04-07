package com.example.myapplication.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.myapplication.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private var listener: HomeFragmentListener? = null
    var newSessionID = ""
    var name = ""
    var session = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       val fragView = inflater.inflate(R.layout.fragment_home, container, false)
        val random = fragView.findViewById<TextView>(R.id.random) as TextView
        val etName = fragView.findViewById<EditText>(R.id.etName) as EditText

        createSessionID(random)
        val btn1 = fragView.findViewById(R.id.btn1) as Button
        val btn2 = fragView.findViewById(R.id.btn2) as Button

        btn1.setOnClickListener {
            name = etName.text.toString()
            if (name.isEmpty()){
                etName.error = "Please Enter a Name"
            }else{
                listener?.detailsEntered(name, newSessionID)
                    btn1.isEnabled = false
            }
        }
        btn2.setOnClickListener {
            name = etName.text.toString()
            session = etSession.text.toString()
            if (name.isEmpty()){
                etName.error = "Please Enter a Name"
            }else{
                listener?.detailsEntered(name, session)
                btn2.isEnabled = false
            }
        }

        return fragView
    }



    private fun createSessionID(random: TextView) {
        val stringLibrary = ('a'..'z').toList().toTypedArray()
        newSessionID = (1..6).map { stringLibrary.random() }.joinToString("")

        random.text = newSessionID
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
