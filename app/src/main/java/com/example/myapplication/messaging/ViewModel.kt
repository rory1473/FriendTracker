package com.example.myapplication.messaging


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData


class ViewModel(app: Application): AndroidViewModel(app) {

    //declare variables
    var db = MessageDatabase.getDatabase(app)
    private var messages: LiveData<List<Message>>

    init {
        //variables are assigned to DAO functions
        messages = db.messageDAO().getMessagesLive()
    }

    fun getMessagesLive(): LiveData<List<Message>> {
        //function called by message fragment to return live album data
        return messages
    }
}