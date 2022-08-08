package me.siddheshkothadi.autofism3.model

import java.util.*

data class PendingUploadFish(
    val timestamp: String,
    val imageUri: String,
    val longitude: String,
    val latitude: String,
    val quantity: String,
    var workId: UUID = UUID.randomUUID()
)
