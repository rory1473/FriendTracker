package com.example.myapplication.messaging

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="messages")

data class Message(@PrimaryKey(autoGenerate = true) val id: Int = 0, val user: String, val message: String, val session: String)