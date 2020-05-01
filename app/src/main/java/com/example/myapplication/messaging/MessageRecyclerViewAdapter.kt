package com.example.myapplication.messaging


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

import com.google.android.material.floatingactionbutton.FloatingActionButton

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class AlbumRecyclerViewAdapter(private val c: Context, private val messages: List<Message>) : CoroutineScope,
    RecyclerView.Adapter<AlbumRecyclerViewAdapter.MyViewHolder>() {
    //declare class variables
    private val TAG = "MessageRecycler"
    private var message: Message? = null
    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(c).inflate(R.layout.single_message, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val getUser = message[position].user
        //set text view with album name and on click listener to display images in that album
        holder.title.text = getUser
        Log.i(TAG, getUser)



    }

    override fun getItemCount(): Int {
        //returns list size
        return messages.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //set views for view holder
        val title = view.findViewById(R.id.title) as TextView
        val body = view.findViewById(R.id.title) as TextView

    }

}