package me.siddheshkothadi.autofism3.workmanager

import UploadStreamRequestBody
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.siddheshkothadi.autofism3.R
import me.siddheshkothadi.autofism3.model.Fish
import me.siddheshkothadi.autofism3.repository.FishRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.io.File
import kotlin.properties.Delegates
import kotlin.random.Random


@ExperimentalPermissionsApi
@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParameters: WorkerParameters,
    private val fishRepository: FishRepository,
) : CoroutineWorker(context, workerParameters) {

    private lateinit var bitmap: Bitmap
    private var id by Delegates.notNull<Int>()

    override suspend fun doWork(): Result {
        return try {
            val imageUri = inputData.getString("IMAGE_URI") ?: return Result.failure()

            Timber.i("ImageUri: $imageUri")

            val stream = context.contentResolver.openInputStream(imageUri.toUri())
            bitmap = BitmapFactory.decodeStream(stream)

            id = Random.nextInt()

            startForegroundService(bitmap, id, 0)

            val fish = fishRepository.getFishByImageUri(imageUri)

            uploadFile(fish)

            fishRepository.deleteFish(fish)

            Timber.i("Deleted Row: $imageUri")

            val imageFile = File(context.filesDir, imageUri.split('/').last())
            imageFile.delete()
            Timber.i("Image Deleted: $imageUri")

            Result.success()
        } catch (exception: Exception) {
            Timber.e(exception)
            Result.retry()
        }
    }

    private fun startForegroundService(image: Bitmap, id: Int, progress: Int) {
        setForegroundAsync(
            ForegroundInfo(
                id,
                NotificationCompat.Builder(context, "upload_channel")
                    .setOnlyAlertOnce(true)
                    .setSmallIcon(R.drawable.ic_baseline_cloud_upload_24)
                    .setLargeIcon(image)
                    .setContentTitle("Uploading Fish Data")
                    .setProgress(100, progress, false)
                    .build()
            )
        )
    }

    private suspend fun uploadFile(fish: Fish) {
        try {
            val stream = context.contentResolver.openInputStream(fish.imageUri.toUri()) ?: return

            val request = UploadStreamRequestBody("image/*", stream) {
                Timber.i("Upload Progress $it")
                startForegroundService(bitmap, id, it)
            }

            val requestImage = MultipartBody.Part.createFormData(
                "file",
                fish.imageUri,
                request
            )

            val requestLongitude = fish.longitude.toRequestBody()
            val requestLatitude = fish.latitude.toRequestBody()
            val requestQuantity = fish.quantity.toRequestBody()
            val requestTimestamp = fish.timeStamp.toRequestBody()

            fishRepository.uploadFishData(
                requestImage,
                requestLongitude,
                requestLatitude,
                requestQuantity,
                requestTimestamp
            )
        } catch (exception: Exception) {
            Timber.e(exception.toString())
            Result.retry()
            return
        }
    }
}