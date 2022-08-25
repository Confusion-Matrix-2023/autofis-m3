package me.siddheshkothadi.autofism3.network

import com.google.gson.annotations.SerializedName

data class SubmitBody(
    @SerializedName("submission_timestamp") val subTimeStamp: String,
    @SerializedName("latitude") val latitude: String,
    @SerializedName("longitude") val longitude: String,
    @SerializedName("device_id") val deviceId: String,

)
