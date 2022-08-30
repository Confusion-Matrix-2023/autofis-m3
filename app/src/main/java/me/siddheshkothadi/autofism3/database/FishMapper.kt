package me.siddheshkothadi.autofism3.database

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
        expertCorrection = expertCorrection,
        quantity = quantity,
        submission_timestamp = submission_timestamp,
        longitude = longitude,
        latitude = latitude,
        image_url = image_url,
        temperature = temperature,
        humidity = humidity,
        pressure = pressure,
        wind_speed = wind_speed,
        wind_direction = wind_direction
    )
}

fun UploadHistoryFish.toUploadHistoryFishEntity(): UploadHistoryFishEntity {
    return UploadHistoryFishEntity(
        id = id,
        submissionId = submissionId,
        prediction = prediction,
        expertCorrection = expertCorrection,
        quantity = quantity,
        submission_timestamp = submission_timestamp,
        longitude = longitude,
        latitude = latitude,
        image_url = image_url,
        temperature = temperature,
        humidity = humidity,
        pressure = pressure,
        wind_speed = wind_speed,
        wind_direction = wind_direction
    )
}