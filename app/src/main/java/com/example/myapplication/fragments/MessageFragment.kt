package com.example.myapplication.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.myapplication.R
import android.R.id.message
import android.app.DownloadManager
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.messaging.Message
import com.example.myapplication.messaging.MessageDatabase
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONException

import org.json.JSONObject



class MessageFragment : Fragment() {


    private var listener: OnFragmentInteractionListener? = null
    lateinit var session: String
    lateinit var user: String
    lateinit var message: String
    lateinit var recyclerView: RecyclerView
    private var messageList = listOf<Message>()
    private lateinit var db: MessageDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragView = inflater.inflate(R.layout.fragment_message, container, false)

        recyclerView =  fragView.findViewById(R.id.recyclerView) as RecyclerView

        val arg = arguments
        user = arg!!.getString("name")!!
        session = arg.getString("session")!!

        val sendBtn = fragView.findViewById(R.id.send_btn) as Button
        sendBtn.setOnClickListener {
            val editText = fragView.findViewById(R.id.text1) as EditText
            message = editText.text.toString()

            Log.d("Message", message)

            val mRequestQue = Volley.newRequestQueue(activity)

            val json = JSONObject()
            try {
                json.put("to", "/topics/" + session)
                val notificationObj = JSONObject()
                notificationObj.put("title", user)
                notificationObj.put("body", message )

                //replace notification with data when went send data
                json.put("notification", notificationObj)

                val URL = "https://fcm.googleapis.com/fcm/send"
                val request = object : JsonObjectRequest(Request.Method.POST, URL, json, {
                        response -> Log.d("MUR", "onResponse: ") }, {
                        error -> Log.d("MUR", "onError: " + error.networkResponse)
                        }
                ) { override fun getHeaders(): Map<String, String>{

                            val header: HashMap<String, String> = HashMap()
                            header.put("content-type", "application/json")
                            header.put("authorization", "key=AAAAHH_Kwx8:APA91bF2bFT0up6nFushgIyDofitdKbvVOQomVuK9ORJHh9WqFakeWGbAIHBqyi95ItVTGFSdJm8ligpKqvHuGoT7U7n7P3dK0aYiM7LgEWe2-oWiozQLNJS5cuyvIH8U4A3P211V5iN")
                            return header
                        }
                }


                mRequestQue.add(request)
            } catch (e: JSONException) {
                e.printStackTrace()
            }


            //val ref = FirebaseDatabase.getInstance()("https://projectserver-29dbd.firebaseio.com")
            //val ref = FirebaseDatabase.getInstance().getReference("messages")
           // val notifications = ref.child("notificationRequests")

            //val notification: HashMap<String, String> = HashMap()
            //notification.put("username", user)
            //notification.put("message", message)
            //notification.put("session", session)

            //notifications.push().setValue(notification)
        }


        return fragView
    }



    override fun onActivityCreated(savedInstanceState: Bundle?){
        super.onActivityCreated(savedInstanceState)
        val activity1 = activity as Context
        db = MessageDatabase.getDatabase(activity1)

        //define view model
        val viewModel = ViewModelProviders.of(activity!!).get(ViewModel::class.java)
        //read live data of albums in view model
        viewModel.getMessagesLive().observe(this, Observer<List<Message>> {
            messageList = it
            //send list of albums to recycler adapter
            Log.i(TAG, messageList.toString())
            recyclerView.layoutManager = LinearLayoutManager(activity1)
            val recyclerViewAdapter = MessageRecyclerViewAdapter(context!!, messageList)
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
