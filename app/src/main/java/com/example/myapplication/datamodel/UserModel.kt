package com.example.myapplication.datamodel

import android.util.Log
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

object UserModel: Observable() {

    private var mValueDataListener: ValueEventListener? = null
    private var mUserList: ArrayList<ReadUser>? = ArrayList()

    private fun getDatabaseRef(): DatabaseReference?{
        return FirebaseDatabase.getInstance().reference.child("user")
    }

    init{
        if(mValueDataListener != null){
            getDatabaseRef()?.removeEventListener(mValueDataListener!!)
        }
        mValueDataListener = null

        mValueDataListener = object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
            try{
                val data: ArrayList<ReadUser> = ArrayList()
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

        getDatabaseRef()?.addValueEventListener(mValueDataListener!!)

    }

    fun getData():ArrayList<ReadUser>?{
        return mUserList
    }






}