package me.siddheshkothadi.autofism3.model

import com.google.gson.annotations.SerializedName

data class UploadHistoryFish(
    @SerializedName("_id") val _id: String,
    @SerializedName("device_id") val device_id: String = "",
    @SerializedName("name") val name: String,
    @SerializedName("image_url") val image_url: String,
    @SerializedName("longitude") val longitude: String,
    @SerializedName("latitude") val latitude: String,
    @SerializedName("quantity") val quantity: String,
    @SerializedName("timestamp") val timestamp: String
)
