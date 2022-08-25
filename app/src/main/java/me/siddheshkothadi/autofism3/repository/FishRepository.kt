package me.siddheshkothadi.autofism3.repository

import android.graphics.Bitmap
import android.graphics.RectF
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import me.siddheshkothadi.autofism3.datastore.BitmapInfo
import me.siddheshkothadi.autofism3.model.PendingUploadFish
import me.siddheshkothadi.autofism3.model.UploadHistoryFish
import me.siddheshkothadi.autofism3.model.weather.Weather
import me.siddheshkothadi.autofism3.network.SubmitBody

interface FishRepository {
    val deviceId: Flow<String>
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

    val boundingBoxes: Flow<List<RectF>>
    val bitmapInfo: Flow<BitmapInfo>

    suspend fun getWeatherData(lat: String, lon: String): JsonObject

    suspend fun submitDetails(imageUri: String)
}