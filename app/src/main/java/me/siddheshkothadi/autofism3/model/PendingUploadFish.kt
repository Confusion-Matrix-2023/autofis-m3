package me.siddheshkothadi.autofism3.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class PendingUploadFish(
    val timestamp: String,
    val imageUri: String,
    val longitude: String,
    val latitude: String,
    val quantity: String,
    var workId: UUID = UUID.randomUUID(),
    // Temp
    var temp: String? = null,
    var pressure: String? = null,
    var humidity: String? = null,
    // Wind
    var speed : String? = null,
    var deg   : String?    = null
)
