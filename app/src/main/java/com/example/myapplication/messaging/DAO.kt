package com.example.myapplication.messaging

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MessageDAO {

    @Query("SELECT * FROM messages")
    fun getMessagesLive(): LiveData<List<Message>>

    @Insert
    fun insert(message: Message) : Long

    @Query("SELECT * FROM messages")
    fun getMessages(): List<Message>

    @Update
    fun update(message: Message) : Int

    @Delete
    fun delete(message: List<Message>) : Int

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