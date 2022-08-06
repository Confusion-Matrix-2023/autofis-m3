package me.siddheshkothadi.autofism3.model

data class UploadHistoryFish(
    val _id: String,
    val device_id: String = "",
    val name: String,
    val image_url: String,
    val longitude: String,
    val latitude: String,
    val quantity: String,
    val timestamp: String
)
