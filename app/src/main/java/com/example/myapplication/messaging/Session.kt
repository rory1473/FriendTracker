package com.example.myapplication.messaging

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="session")

data class Session(@PrimaryKey(autoGenerate = false)var id: Int, var curSession: String)