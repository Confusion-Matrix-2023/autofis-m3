package me.siddheshkothadi.autofism3.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class PendingUploadFishEntity(
    @PrimaryKey val timestamp: String,
    val imageUri: String,
    val longitude: String,
    val latitude: String,
    val quantity: String,
    val workId: UUID
)
