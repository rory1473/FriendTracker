package com.example.myapplication.datamodel

import com.google.firebase.database.DataSnapshot

class ReadPhoto(dataSnapshot: DataSnapshot) {

    lateinit var id: String
    lateinit var name: String
    lateinit var image: String
    lateinit var session: String
    lateinit var lat: String
    lateinit var long: String



    init{
        try{
            //sets snapshot into hash map where the values can be stored from
            @Suppress("UNCHECKED_CAST")
            val data: HashMap<String, Any> = dataSnapshot.value as HashMap<String, Any>
            id = dataSnapshot.key ?: ""
            name = data["name"] as String
            image = data["image"] as String
            session = data["session"] as String
            lat = data["lat"] as String
            long = data["long"] as String


        }catch(e: Exception){
            e.printStackTrace()
        }
    }




}