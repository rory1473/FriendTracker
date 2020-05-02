package com.example.myapplication.datamodel

import android.util.Log
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

object PhotoModel: Observable() {

    private var mValueDataListener: ValueEventListener? = null
    private var mPhotoList: ArrayList<ReadPhoto>? = ArrayList()

    private fun getDatabaseRef(): DatabaseReference?{
        return FirebaseDatabase.getInstance().reference.child("photo")
    }

    init{
        if(mValueDataListener != null){
            getDatabaseRef()?.removeEventListener(mValueDataListener!!)
        }
        mValueDataListener = null

        mValueDataListener = object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try{
                    val data: ArrayList<ReadPhoto> = ArrayList()
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

    fun getData():ArrayList<ReadPhoto>?{
        return mPhotoList
    }


}