package com.example.myapplication.datamodel

import android.util.Log
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

object UserModel: Observable() {

    private var mValueDataListener: ValueEventListener? = null
    private var mUserList: ArrayList<ReadUser>? = ArrayList()

    //get firebase database reference to users
    private fun getDatabaseRef(): DatabaseReference?{
        return FirebaseDatabase.getInstance().reference.child("user")
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
                val data: ArrayList<ReadUser> = ArrayList()
                //add each child snapshot to the array through the ReadUser class
                if (dataSnapshot != null){
                    for (postSnapshot: DataSnapshot in dataSnapshot.children) {
                        try{
                            data.add(ReadUser(postSnapshot))

                        } catch(e: Exception){
                            e.printStackTrace()
                        }
                    }
                    mUserList = data
                    Log.i("read data", "number of read data: " + mUserList!!)
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
    //function to call user array list from MapFragment
    fun getData():ArrayList<ReadUser>?{
        return mUserList
    }


}