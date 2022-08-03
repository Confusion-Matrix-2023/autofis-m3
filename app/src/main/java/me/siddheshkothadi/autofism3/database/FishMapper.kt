package me.siddheshkothadi.autofism3.database

import androidx.room.PrimaryKey
import me.siddheshkothadi.autofism3.model.Fish

fun FishEntity.toFish(): Fish {
    return Fish(
        imageUri = imageUri,
        longitude = longitude,
        latitude = latitude,
        quantity = quantity,
        timeStamp = timeStamp
    )
}

fun Fish.toFishEntity(): FishEntity {
    return FishEntity(
        imageUri = imageUri,
        longitude = longitude,
        latitude = latitude,
        quantity = quantity,
        timeStamp = timeStamp
    )
}