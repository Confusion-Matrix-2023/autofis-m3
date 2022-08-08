package me.siddheshkothadi.autofism3.repository

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow
import me.siddheshkothadi.autofism3.model.PendingUploadFish
import me.siddheshkothadi.autofism3.model.UploadHistoryFish

interface FishRepository {
    suspend fun enqueueUpload(fish: PendingUploadFish)

    fun getPendingUploads(): Flow<List<PendingUploadFish>>
    suspend fun getPendingUploadByImageUri(imageUri: String): PendingUploadFish
    suspend fun insertFish(fish: PendingUploadFish)
    suspend fun deleteFish(fish: PendingUploadFish)

    suspend fun uploadFishData(
        imageUri: String,
        showNotification: (id: Int, bitmap: Bitmap, progress: Int) -> Unit
    )

    suspend fun fetchUploadHistory()
    fun getUploadHistory() : Flow<List<UploadHistoryFish>>
}