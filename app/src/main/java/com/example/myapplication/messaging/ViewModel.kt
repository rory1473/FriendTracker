package com.example.myapplication.messaging


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.messaging.Message
import com.example.myapplication.messaging.Session
import com.example.myapplication.messaging.MessageDatabase

class ViewModel(app: Application): AndroidViewModel(app) {


    var db = MessageDatabase.getDatabase(app)

    private var messages: LiveData<List<Message>>
   // private var sessionOb:  Session?
    //private var session =""

    init {
       // sessionOb = db.messageDAO().getSessionByID(1)
       // session = sessionOb!!.curSession
        messages = db.messageDAO().getMessagesLive()
    }

   // fun getSessionByID(): Session? {
    //    return sessionOb
    //}

    fun getMessagesLive(): LiveData<List<Message>> {
        return messages
    }


}