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
    val workId: UUID,
    // Temp
    var temp: Double? = null,
    var feelsLike: Double? = null,
    var tempMin: Double? = null,
    var tempMax: Double? = null,
    var pressure: Int? = null,
    var humidity: Int? = null,
    // Wind
    var speed : Double? = null,
    var deg   : Int?    = null
)
