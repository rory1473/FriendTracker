package com.example.myapplication.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.messaging.MessageDatabase
import com.example.myapplication.messaging.Session
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import android.widget.RadioGroup




class HomeFragment : Fragment() {
    //declare class variables
    private val TAG = "HomeFragment"
    private var listener: HomeFragmentListener? = null
    var newSessionID = ""
    var name = ""
    var session = ""
    var color = ""
    var stringRandom = ""
    private lateinit var db: MessageDatabase
    private var sessionList = listOf<Session>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       val fragView = inflater.inflate(R.layout.fragment_home, container, false)
        val random = fragView.findViewById(R.id.random) as TextView
        val etName = fragView.findViewById(R.id.etName) as EditText
        //set bottom navigation as visible
        val bottomNavigationView = activity!!.findViewById(R.id.nav_view) as BottomNavigationView
        bottomNavigationView.visibility = View.INVISIBLE

        //reset instance ID
        Thread(Runnable {
            run{
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }).start()

        //when fragment is created the previous session data is cleared from database
        val activity1 = activity as Context
        db = MessageDatabase.getDatabase(activity1)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                sessionList = db.messageDAO().getSession()
            }
            var sessionID: Int? = null
            withContext(Dispatchers.IO) {
                if (sessionList != null) {
                    for (data in sessionList) {
                        sessionID = db.messageDAO().deleteSession(data)
                    }}
                Log.i(TAG, sessionID.toString())
            }
            //new empty session record is added to database
            var sessionID2: Long? = null
            val newSession = Session(id = 1, curSession = "", user = "")
            withContext(Dispatchers.IO) {
                sessionID2 = db.messageDAO().insertSession(newSession)
            }
            Log.i(TAG, sessionID2.toString())
        }
        //function call creates random string
        createSessionID(random)

        //radio group view is defined to set the value of which item is selected
        val radioGroup = fragView.findViewById(R.id.radioGroup) as RadioGroup
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.red) {
                color = "red"
            }
            else if (checkedId == R.id.blue) {
                color = "blue"
            }
            else if (checkedId == R.id.green) {
                color = "green"
            }
            else {
                color = "orange"
            }
        }

        val joinSession = fragView.findViewById(R.id.joinSession) as Button
        val newSession = fragView.findViewById(R.id.newSession) as Button
        //button checks name isn't blank and sends details to interface with random string
        newSession.setOnClickListener {
            name = etName.text.toString()
            if (name.isEmpty()){
                etName.error = "Please Enter a Name"
            }else{
                listener?.detailsEntered(name, stringRandom, color)
                    joinSession.isEnabled = false
            }
        }
        //button checks name and session isn't blank and sends details to interface
        joinSession.setOnClickListener {
            name = etName.text.toString()
            session = etSession.text.toString()
            if (name.isEmpty()){
                etName.error = "Please Enter a Name"
            }
            if (session.isEmpty()){
                etSession.error = "Please Enter the Session"
            }else{
                listener?.detailsEntered(name, session, color)
                newSession.isEnabled = false
            }
        }

        return fragView
    }



    private fun createSessionID(random: TextView) {
        //random 6 character lowercase string is generated
        val stringLibrary = ('a'..'z').toList().toTypedArray()
        stringRandom = (1..6).map { stringLibrary.random() }.joinToString("")
        newSessionID = "New Session ID: "+stringRandom
        random.text = newSessionID
    }



    interface HomeFragmentListener {
        fun detailsEntered(homeName: String, homeSession: String , homeColor: String)

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
