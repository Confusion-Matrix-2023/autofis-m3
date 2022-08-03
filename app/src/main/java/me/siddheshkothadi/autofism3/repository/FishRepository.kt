package me.siddheshkothadi.autofism3.repository

import kotlinx.coroutines.flow.Flow
import me.siddheshkothadi.autofism3.model.Fish
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface FishRepository {
    fun getAllFish(): Flow<List<Fish>>

    suspend fun getFishList(): List<Fish>

    suspend fun getFishByImageUri(imageUri: String): Fish

    suspend fun insertFish(fish: Fish)

    suspend fun deleteFish(fish: Fish)

    suspend fun deleteAllFish()

    suspend fun uploadFishData(
        image: MultipartBody.Part,
        longitude: RequestBody,
        latitude: RequestBody,
        quantity: RequestBody,
        timestamp: RequestBody,
    )

    suspend fun getHistory() : List<Fish>
}