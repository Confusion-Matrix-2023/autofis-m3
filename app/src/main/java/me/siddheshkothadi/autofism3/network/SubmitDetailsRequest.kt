package me.siddheshkothadi.autofism3.network

data class SubmitDetailsRequest(
    val submission_timestamp: String,
    val latitude: String,
    val image_url: String,
    val longitude: String,
    val device_id: String

)
