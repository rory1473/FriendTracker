package com.example.myapplication.messaging

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MessageDAO {

    //@Query("SELECT * FROM messages WHERE album=:album")
    //fun getImageByAlbum(album: Int): List<Message>

    @Query("SELECT * FROM messages WHERE id=:id")
    fun getImageByID(id: Int): Message?

    @Query("SELECT * FROM messages")
    fun getAllImages(): List<Message>

    @Query("SELECT * FROM messages")
    fun getMessagesLive(): LiveData<List<Message>>

    @Insert
    fun insert(images: Message) : Long

    @Update
    fun update(images: Message) : Int

    @Delete
    fun delete(images: Message) : Int

    @Query("SELECT * FROM session")
    fun getSession(): List<Session>

    @Query("SELECT * FROM session WHERE id=:id")
    fun getSessionByID(id: Int): Session?

    @Query("SELECT * FROM session WHERE id=:id")
    fun getSessionByIDLive(id: Int): LiveData<Session>

    @Insert
    fun insertSession(session: Session) : Long

    @Update
    fun updateSession(session: Session) : Int

    @Delete
    fun deleteSession(session: Session) : Int

}