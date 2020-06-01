package com.example.myapplication.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.myapplication.R
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.messaging.Message
import com.example.myapplication.messaging.MessageDatabase
import com.example.myapplication.messaging.MessageRecyclerViewAdapter
import com.example.myapplication.messaging.ViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONException
import org.json.JSONObject



class MessageFragment : Fragment() {
    //declare class variables
    private val TAG = "MessageFragment"
    private var listener: OnFragmentInteractionListener? = null
    lateinit var session: String
    lateinit var user: String
    lateinit var message: String
    lateinit var recyclerView: RecyclerView
    private var allMessageList = listOf<Message>()
    private var messageList = mutableListOf<Message>()
    private lateinit var db: MessageDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragView = inflater.inflate(R.layout.fragment_message, container, false)

        recyclerView =  fragView.findViewById(R.id.recyclerView) as RecyclerView
        //set bottom navigation as visible
        val bottomNavigationView = activity!!.findViewById(R.id.nav_view) as BottomNavigationView
        bottomNavigationView.visibility = View.VISIBLE

        val title = fragView.findViewById(R.id.title) as TextView
        title.bringToFront()

        //set back button to return to previous fragment
        val backButton = fragView.findViewById(R.id.back_btn) as FloatingActionButton
        backButton.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
            backButton.isEnabled = false
        }

        val arg = arguments
        user = arg!!.getString("name")!!
        session = arg.getString("session")!!

        //on click listener sends message to group chat
        val sendBtn = fragView.findViewById(R.id.send_btn) as FloatingActionButton
        sendBtn.setOnClickListener {
            val messageText = fragView.findViewById(R.id.messageText) as EditText
            message = messageText.text.toString()
            if (message != "") {
                messageText.text.clear()
                Log.d(TAG, message)
                //set request queue
                val mRequestQue = Volley.newRequestQueue(activity)
                //post request put into JSON object
                val json = JSONObject()
                try {
                    json.put("to", "/topics/" + session)
                    val notificationObj = JSONObject()
                    notificationObj.put("title", user)
                    notificationObj.put("body", message)
                    json.put("notification", notificationObj)

                    //receiving firebase address handles post request
                    val URL = "https://fcm.googleapis.com/fcm/send"
                    val request = object : JsonObjectRequest(Request.Method.POST, URL, json, {
                        Log.d(TAG, "onResponse: ")
                    }, { error -> Log.d("MUR", "onError: " + error.networkResponse) }
                    ) {
                        override fun getHeaders(): Map<String, String> {
                            //set header values
                            val header: HashMap<String, String> = HashMap()
                            header.put("content-type", "application/json")
                            header.put(
                                "authorization",
                                "key=AAAAHH_Kwx8:APA91bF2bFT0up6nFushgIyDofitdKbvVOQomVuK9ORJHh9WqFakeWGbAIHBqyi95ItVTGFSdJm8ligpKqvHuGoT7U7n7P3dK0aYiM7LgEWe2-oWiozQLNJS5cuyvIH8U4A3P211V5iN"
                            )
                            return header
                        }
                    }
                    mRequestQue.add(request)
                } catch (e: JSONException) {
                    //display error on console
                    e.printStackTrace()
                }
            }
        }

        return fragView
    }



    override fun onActivityCreated(savedInstanceState: Bundle?){
        super.onActivityCreated(savedInstanceState)
        val activity1 = activity as Context
        db = MessageDatabase.getDatabase(activity1)

        //define view model
        val viewModel = ViewModelProviders.of(activity!!).get(ViewModel::class.java)
        //read live data of messages in view model
        viewModel.getMessagesLive().observe(this, Observer<List<Message>> {
            allMessageList = it
            Log.i(TAG, allMessageList.toString())
            //send list of messages to recycler adapter
            for(message in allMessageList){
                if(messageList.contains(message)){
                } else{
                    messageList.add(message)
            }
            }

            Log.i(TAG, messageList.toString())
            val layout = LinearLayoutManager(context)
            layout.stackFromEnd = true     // items gravity sticks to bottom
            layout.reverseLayout = false
            recyclerView.layoutManager = layout
            val recyclerViewAdapter = MessageRecyclerViewAdapter(context!!, messageList, session, user)
            recyclerView.adapter = recyclerViewAdapter
        })
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
        fun newInstance() = MessageFragment()
    }
}
