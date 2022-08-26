package me.siddheshkothadi.autofism3.model

import com.google.gson.annotations.SerializedName

data class UploadHistoryFish(
    @SerializedName("id") val id: String,
    @SerializedName("submission_id") val submissionId: String,
    @SerializedName("prediction") val prediction: String?,
    @SerializedName("confidence") val confidence: String?,
    @SerializedName("expert_correction") val expertCorrection: String?
//    @SerializedName("_id") val _id: String,
//    @SerializedName("device_id") val device_id: String = "",
//    @SerializedName("name") val name: String,
//    @SerializedName("image_url") val image_url: String,
//    @SerializedName("longitude") val longitude: String,
//    @SerializedName("latitude") val latitude: String,
//    @SerializedName("quantity") val quantity: String,
//    @SerializedName("timestamp") val timestamp: String
)
