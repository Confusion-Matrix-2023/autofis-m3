package me.siddheshkothadi.autofism3.model

import com.google.gson.annotations.SerializedName

data class UploadHistoryFish(
    @SerializedName("id") val id: String,
    @SerializedName("submission_id") val submissionId: String?,
    @SerializedName("prediction") val prediction: String?,
    @SerializedName("expert_correction") val expertCorrection: String?,
    @SerializedName("quantity") val quantity: String?,
    @SerializedName("submission_timestamp") val submission_timestamp: String?,
    @SerializedName("longitude") val longitude: String?,
    @SerializedName("latitude") val latitude: String?,
    @SerializedName("image_url") val image_url: String?,
    @SerializedName("temperature") val temperature: String?,
    @SerializedName("humidity") val humidity: String?,
    @SerializedName("pressure") val pressure: String?,
    @SerializedName("wind_speed") val wind_speed: String?,
    @SerializedName("wind_direction") val wind_direction: String?,
)
