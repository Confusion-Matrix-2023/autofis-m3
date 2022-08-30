package me.siddheshkothadi.autofism3.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class UploadHistoryFishEntity(
    @PrimaryKey val id: String,
    val submissionId: String?,
    val prediction: String?,
    val expertCorrection: String?,
    val quantity: String?,
    val submission_timestamp: String?,
    val longitude: String?,
    val latitude: String?,
    val image_url: String?,
    val temperature: String?,
    val humidity: String?,
    val pressure: String?,
    val wind_speed: String?,
    val wind_direction: String?,
)
