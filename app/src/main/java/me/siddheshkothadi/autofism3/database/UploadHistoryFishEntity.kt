package me.siddheshkothadi.autofism3.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UploadHistoryFishEntity(
    @PrimaryKey val _id: String = "",
    val name: String = "",
    val image_url: String = "",
    val longitude: String,
    val latitude: String,
    val quantity: String,
    val timestamp: String
)
