package com.example.myapplication.datamodel

import android.util.Log
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

object PhotoModel: Observable() {

    private var mValueDataListener: ValueEventListener? = null
    private var mPhotoList: ArrayList<ReadPhoto>? = ArrayList()

    //get firebase database reference to photos
    private fun getDatabaseRef(): DatabaseReference?{
        return FirebaseDatabase.getInstance().reference.child("photo")
    }

    init{
        //remove previous listener
        if(mValueDataListener != null){
            getDatabaseRef()?.removeEventListener(mValueDataListener!!)
        }
        mValueDataListener = null
        //listener object calls when data inside the database is changed
        mValueDataListener = object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try{
                    val data: ArrayList<ReadPhoto> = ArrayList()
                    //add each child snapshot to the array through the ReadUser class
                    if (dataSnapshot != null){
                        for (postSnapshot: DataSnapshot in dataSnapshot.children) {
                            try{
                                data.add(ReadPhoto(postSnapshot))

                            } catch(e: Exception){
                                e.printStackTrace()
                            }



                        }
                        mPhotoList = data
                        Log.i("read data", "number of read photos: " + mPhotoList!!)
                        //notifies MapFragment that data has changed
                        setChanged()
                        notifyObservers()
                    }

                } catch(e: Exception){
                    e.printStackTrace()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.i("UserModel", "data update cancelled: ${databaseError.toException()}")
            }

        }
        //listener is added to database
        getDatabaseRef()?.addValueEventListener(mValueDataListener!!)

    }
    //function to call photo array list from MapFragment
    fun getData():ArrayList<ReadPhoto>?{
        return mPhotoList
    }


}