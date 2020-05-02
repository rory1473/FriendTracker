package com.example.myapplication.messaging


import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R



class MessageRecyclerViewAdapter(private val c: Context, private val messages: List<Message>, private val session: String, private val user: String) :  RecyclerView.Adapter<MessageRecyclerViewAdapter.MyViewHolder>() {
    //declare class variables
    private val TAG = "MessageRecycler"


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(c).inflate(R.layout.single_message, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if(user == messages[position].user){
            holder.message.gravity = Gravity.RIGHT
        }

        if(session == messages[position].session) {
            val getUser = messages[position].user
            holder.title.text = getUser
            Log.i(TAG, getUser)
            val getMessage = messages[position].message
            holder.body.text = getMessage
            Log.i(TAG, getMessage)
        }

    }

    override fun getItemCount(): Int {
        //returns list size
        return messages.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //set views for view holder
        val message = view.findViewById(R.id.message) as LinearLayout
        val title = view.findViewById(R.id.title) as TextView
        val body = view.findViewById(R.id.body) as TextView

    }

}