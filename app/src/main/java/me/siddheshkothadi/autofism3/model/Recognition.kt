package me.siddheshkothadi.autofism3.model

import com.google.gson.annotations.SerializedName

data class Recognition(
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


//"submission_created_at": "2022-08-26T08:41:50.307122Z",
//"quantity": null,
//"submission_timestamp": "2022-08-16T10:44:00Z",
//"latitude": 24.61879615,
//"longitude": 76.81716721851998,
//"image_url": "/images/f1_6XUZMUh.jpg",
//"temperature": null,
//"humidity": "32",
//"pressure": null,
//"wind_speed": "222",
//"wind_direction": "Nt"
)
