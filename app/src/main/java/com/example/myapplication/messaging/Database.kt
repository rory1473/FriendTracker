package com.example.myapplication.messaging

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Message::class, Session::class), version = 1, exportSchema = false)
abstract class MessageDatabase: RoomDatabase() {
    abstract fun messageDAO(): MessageDAO
    //builds room database with DAO and tables
    companion object {
        private var instance: MessageDatabase? = null

        fun getDatabase(ctx: Context): MessageDatabase {
            var tmpInstance = instance
            if (tmpInstance == null) {
                tmpInstance = Room.databaseBuilder(
                    ctx.applicationContext,
                    MessageDatabase::class.java,
                    "messageDatabase"
                ).build()
                instance = tmpInstance
            }
            return tmpInstance
        }
    }
}