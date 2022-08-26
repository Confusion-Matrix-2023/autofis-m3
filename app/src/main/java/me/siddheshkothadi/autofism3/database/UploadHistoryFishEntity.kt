package me.siddheshkothadi.autofism3.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class UploadHistoryFishEntity(
    @PrimaryKey val id: String,
    val submissionId: String,
    val prediction: String?,
    val confidence: String?,
    val expertCorrection: String?
)
