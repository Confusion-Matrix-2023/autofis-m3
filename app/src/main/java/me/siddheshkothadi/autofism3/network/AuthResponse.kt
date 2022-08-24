package me.siddheshkothadi.autofism3.network

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("id") val id: String,
    @SerializedName("device_name") val deviceName: String,
    @SerializedName("device_created_at") val deviceCreatedAt: String,
    @SerializedName("device_key") val deviceKey: String,

//"id": "01a26d90-51ab-4b71-a590-d62af8cd4aae"
//"device_name": "test 01",
//"device_created_at": "2022-08-23T03:08:14.898002Z",
//"device_key": "213456432"

)
