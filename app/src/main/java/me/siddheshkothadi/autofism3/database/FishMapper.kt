package me.siddheshkothadi.autofism3.database

import androidx.room.PrimaryKey
import me.siddheshkothadi.autofism3.model.PendingUploadFish
import me.siddheshkothadi.autofism3.model.UploadHistoryFish

fun PendingUploadFishEntity.toPendingUploadFish(): PendingUploadFish {
    return PendingUploadFish(
        imageUri = imageUri,
        longitude = longitude,
        latitude = latitude,
        quantity = quantity,
        timestamp = timestamp,
        workId = workId,
        temp = temp,
        pressure = pressure,
        humidity = humidity,
        speed = speed,
        deg = deg
    )
}

fun PendingUploadFish.toPendingUploadFishEntity(): PendingUploadFishEntity {
    return PendingUploadFishEntity(
        imageUri = imageUri,
        longitude = longitude,
        latitude = latitude,
        quantity = quantity,
        timestamp = timestamp,
        workId = workId,
        temp = temp,
        pressure = pressure,
        humidity = humidity,
        speed = speed,
        deg = deg
    )
}

fun UploadHistoryFishEntity.toUploadHistoryFish(): UploadHistoryFish {
    return UploadHistoryFish(
        id = id,
        submissionId = submissionId,
        prediction = prediction,
        confidence = confidence,
        expertCorrection = expertCorrection
    )
}

fun UploadHistoryFish.toUploadHistoryFishEntity(): UploadHistoryFishEntity {
    return UploadHistoryFishEntity(
        id = id,
        submissionId = submissionId,
        prediction = prediction,
        confidence = confidence,
        expertCorrection = expertCorrection
    )
}