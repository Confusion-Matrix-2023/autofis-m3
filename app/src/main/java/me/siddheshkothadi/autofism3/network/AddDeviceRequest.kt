package me.siddheshkothadi.autofism3.network

import com.google.gson.annotations.SerializedName

data class AddDeviceRequest(
    @SerializedName("device_name") val deviceName: String,
    @SerializedName("device_key") val deviceKey: String
)
