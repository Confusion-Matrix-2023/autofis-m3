package me.siddheshkothadi.autofism3.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import androidx.work.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import me.siddheshkothadi.autofism3.FishApplication
import me.siddheshkothadi.autofism3.database.*
import me.siddheshkothadi.autofism3.datastore.BitmapInfo
import me.siddheshkothadi.autofism3.datastore.LocalDataStore
import me.siddheshkothadi.autofism3.model.PendingUploadFish
import me.siddheshkothadi.autofism3.model.UploadHistoryFish
import me.siddheshkothadi.autofism3.network.FileAPI
import me.siddheshkothadi.autofism3.network.UploadStreamRequestBody
import me.siddheshkothadi.autofism3.workmanager.UploadWorker
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class FishRepositoryImpl(
    private val pendingUploadFishDAO: PendingUploadFishDAO,
    private val uploadHistoryFishDAO: UploadHistoryFishDAO,
    private val localDataStore: LocalDataStore,
    private val fileAPI: FileAPI,
    private val context: FishApplication
) : FishRepository {
    private val workManager = WorkManager.getInstance(context)

    override val boundingBoxes: Flow<List<RectF>>
        get() = localDataStore.recognitions.map { recognitions ->
            recognitions.locationList.map { boundingBox ->
                RectF(
                    boundingBox.left,
                    boundingBox.top,
                    boundingBox.right,
                    boundingBox.bottom
                )
            }
        }
    override val bitmapInfo: Flow<BitmapInfo>
        get() = localDataStore.bitmapInfo

    @OptIn(ExperimentalPermissionsApi::class)
    private fun generateWorkRequest(
        uri: String,
        tag: String
    ): WorkRequest {
        return OneTimeWorkRequestBuilder<UploadWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED
                    )
                    .build()
            )
            .addTag(tag)
            .setInputData(
                workDataOf(
                    IMAGE_URI to uri
                )
            )
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
    }

    private suspend fun getBearerToken(): String {
        val bearerToken = localDataStore.bearerToken.first()
        if (bearerToken.isNotBlank()) return bearerToken
        localDataStore.setDeviceIdAndBearerToken()
        return localDataStore.bearerToken.first()
    }

    override suspend fun enqueueUpload(fish: PendingUploadFish) {
        val uploadRequest = generateWorkRequest(fish.imageUri, TAG)
        val pendingUploadFish = fish.copy(workId = uploadRequest.id)
        insertFish(pendingUploadFish)
        workManager.enqueue(uploadRequest)
    }

    private fun deleteFishImage(imageUri: String) {
        val imageFile = File(context.filesDir, imageUri.split('/').last())
        imageFile.delete()
        Timber.i("Image Deleted: $imageUri")
    }

    override fun getPendingUploads(): Flow<List<PendingUploadFish>> {
        return pendingUploadFishDAO.getAll().map { pendingUploads ->
            pendingUploads.map { pendingUpload ->
                pendingUpload.toPendingUploadFish()
            }
        }
    }

    override fun getUploadHistory(): Flow<List<UploadHistoryFish>> {
        return uploadHistoryFishDAO.getAll().map { uploadHistoryList ->
            uploadHistoryList.map {
                it.toUploadHistoryFish()
            }
        }
    }

    override suspend fun getPendingUploadByImageUri(imageUri: String): PendingUploadFish {
        return pendingUploadFishDAO.getByImageUri(imageUri).toPendingUploadFish()
    }

    override suspend fun insertFish(fish: PendingUploadFish) {
        pendingUploadFishDAO.insert(fish.toPendingUploadFishEntity())
    }

    override suspend fun deleteFish(fish: PendingUploadFish) {
        try {
            pendingUploadFishDAO.delete(fish.toPendingUploadFishEntity())
            Timber.i("Deleted Row: $fish")
        } catch (e: Exception) {
            Timber.e(e)
            throw e
        }
    }

    override suspend fun uploadFishData(
        imageUri: String,
        showNotification: (id: Int, bitmap: Bitmap, progress: Int) -> Unit
    ) {
        try {
            val imageFile = File(context.filesDir, imageUri.split('/').last())
            val bitmap = BitmapFactory.decodeStream(imageFile.inputStream())
            val id = Random.nextInt()

            showNotification(id, bitmap, 0)
            val fish = getPendingUploadByImageUri(imageUri)

            val request = UploadStreamRequestBody("image/*", imageFile.inputStream()) { progress ->
                Timber.i("Upload Progress $progress")
                showNotification(id, bitmap, progress)
            }

            val requestImage = MultipartBody.Part.createFormData(
                "file",
                imageFile.name,
                request
            )

            val requestLongitude = fish.longitude.toRequestBody()
            val requestLatitude = fish.latitude.toRequestBody()
            val requestQuantity = fish.quantity.toRequestBody()
            val requestTimestamp = fish.timestamp.toRequestBody()

            val bearerToken = getBearerToken()

            fileAPI.uploadData(
                bearerToken,
                requestImage,
                requestLongitude,
                requestLatitude,
                requestQuantity,
                requestTimestamp
            )

            deleteFish(fish)
            deleteFishImage(fish.imageUri)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun fetchUploadHistory() {
        val bearerToken = getBearerToken()

        try {
            val updatedUploadHistory = fileAPI.getHistory(bearerToken)
            uploadHistoryFishDAO.deleteAll()
            uploadHistoryFishDAO.insertMany(
                updatedUploadHistory.map {
                    it.toUploadHistoryFishEntity()
                }
            )
        } catch (exception: Exception) {
            Timber.e(exception)
        }
    }

    companion object {
        const val IMAGE_URI = "IMAGE_URI"
        const val TAG = "UPLOAD_REQUEST"
    }
}