package me.siddheshkothadi.autofism3.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.widget.Toast
import androidx.work.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.siddheshkothadi.autofism3.FishApplication
import me.siddheshkothadi.autofism3.database.*
import me.siddheshkothadi.autofism3.datastore.BitmapInfo
import me.siddheshkothadi.autofism3.datastore.LocalDataStore
import me.siddheshkothadi.autofism3.model.PendingUploadFish
import me.siddheshkothadi.autofism3.model.UploadHistoryFish
import me.siddheshkothadi.autofism3.model.weather.Weather
import me.siddheshkothadi.autofism3.network.*
import me.siddheshkothadi.autofism3.utils.DateUtils
import me.siddheshkothadi.autofism3.workmanager.UploadWorker
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class FishRepositoryImpl(
    private val pendingUploadFishDAO: PendingUploadFishDAO,
    private val uploadHistoryFishDAO: UploadHistoryFishDAO,
    private val localDataStore: LocalDataStore,
    private val fileAPI: FileAPI,
    private val awsFileAPI: AWSFileAPI,
    private val weatherAPI: WeatherAPI,
    private val s3Bucket: S3Bucket,
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
        Timber.tag("Sid").i(bearerToken)
        if (bearerToken.isNotBlank()) return bearerToken
        localDataStore.setDeviceKeyNameAndBearerToken()
        val newBearerToken = localDataStore.bearerToken.first()
        Timber.tag("Sid").i(newBearerToken)
        try {
            val response = awsFileAPI.checkDevice(newBearerToken)
            Timber.tag("Sid").i(response.toString())
            localDataStore.apply {
                setDeviceKey(response.deviceKey)
                setDeviceName(response.deviceName)
                setId(response.id)
            }
            Timber.i("Success $newBearerToken")
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    response.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: HttpException) {
            val dName = localDataStore.deviceName.first()
            val dKey = localDataStore.deviceKey.first()
            val response = awsFileAPI.addNewDevice(AddDeviceRequest(dName, dKey))
            Timber.i(response.toString())
            localDataStore.apply {
                setDeviceKey(response.deviceKey)
                setDeviceName(response.deviceName)
                setId(response.id)
            }
            Timber.i("Failure $newBearerToken")
        }

        return newBearerToken
    }

    override suspend fun getWeatherData(lat: String, lon: String): JsonObject {
//        return weatherAPI.getWeatherData(lat,lon,"2851a90b716da669a9118af4c2b59341")
        return weatherAPI.getWeatherData()
    }

    override val deviceId: Flow<String>
        get() = localDataStore.id

    override suspend fun submitDetails(imageUri: String) {
        val token = getBearerToken()
        val deviceId = localDataStore.id.first()
        val tempFish = getPendingUploadByImageUri(imageUri)

        Timber.i(token)
        Timber.i(tempFish.toString())
        Timber.i(deviceId)

        val imageFile: File = File(context.filesDir, imageUri.split('/').last())

        val request = UploadStreamRequestBody("image/jpeg", imageFile.inputStream()) { progress ->
            Timber.i(" $progress")
        }

        val requestImage = MultipartBody.Part.createFormData(
            "image_url",
            imageFile.name,
            request
        )

        val requestLongitude = tempFish.longitude.toRequestBody()
        val requestLatitude = tempFish.latitude.toRequestBody()
        val requestTimestamp = DateUtils.getSubmissionTimeStamp(tempFish.timestamp.toLong()).toRequestBody()
        val requestDeviceId = deviceId.toRequestBody()

        val response = awsFileAPI.submitDetailsWithImage(
            token,
            requestImage,
            requestLongitude,
            requestLatitude,
            requestDeviceId,
            requestTimestamp
        )

        Timber.i(response.toString())
//        val requestBody: RequestBody = RequestBody.create(
//            "image/jpeg".toMediaTypeOrNull(),
//            file
//        )

//        val fileUri = file.getUri(context)

//        fileUri?.let {
////            val reqBody = context.contentResolver.readAsRequestBody(it)
//            val reqBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
//            val mimeType = context.contentResolver.getType(it)
//
//            Timber.i(reqBody.toString())
//            Timber.i(mimeType.toString())
//
//            if (mimeType != null) {
//                s3Bucket.uploadImage(url, "image/jpeg", reqBody)
//            }
//        }

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

            val request = UploadStreamRequestBody("image/jpeg", imageFile.inputStream()) { progress ->
                Timber.i("Upload Progress $progress")
                showNotification(id, bitmap, progress)
            }

            val requestImage = MultipartBody.Part.createFormData(
                "image_url",
                imageFile.name,
                request
            )
            val bearerToken = getBearerToken()
            val deviceId = localDataStore.id.first()

            val requestLongitude = fish.longitude.toRequestBody()
            val requestLatitude = fish.latitude.toRequestBody()
            val requestTimestamp = DateUtils.getSubmissionTimeStamp(fish.timestamp.toLong()).toRequestBody()
            val requestDeviceId = deviceId.toRequestBody()

            awsFileAPI.submitDetailsWithImage(
                bearerToken,
                requestImage,
                requestLongitude,
                requestLatitude,
                requestDeviceId,
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