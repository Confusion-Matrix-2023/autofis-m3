package me.siddheshkothadi.autofism3.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FishEntity(
    @PrimaryKey val imageUri: String,
    val longitude: String,
    val latitude: String,
    val quantity: String,
    val timeStamp: String
)
