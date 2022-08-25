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
