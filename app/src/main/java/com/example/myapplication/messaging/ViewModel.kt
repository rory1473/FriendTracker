package com.example.myapplication.messaging


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel


class ViewModel(app: Application): AndroidViewModel(app) {

    //declares variables
    var db = MessageDatabase.getDatabase(app)
    private var messages: LiveData<List<Message>>
    //private var images: LiveData<List<Session>>
    private var session:  LiveData<Session>

    init {
        //variables are assigned to DAO functions
        //albums = db.messageDAO().getAllAlbumsLive()
        messages = db.messageDAO().getAllMessagesLive()
        session = db.messageDAO().getSessionByIDLive(1)
    }

    fun getSessionByIDLive(): LiveData<Session> {
        return session
    }

    fun getAllMessagesLive(): LiveData<List<Message>> {
        return messages
    }


}