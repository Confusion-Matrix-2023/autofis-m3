package me.siddheshkothadi.autofism3.workmanager

import android.content.Context
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import me.siddheshkothadi.autofism3.R
import me.siddheshkothadi.autofism3.repository.FishRepository
import timber.log.Timber


@ExperimentalPermissionsApi
@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParameters: WorkerParameters,
    private val fishRepository: FishRepository,
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return try {
            val imageUri = inputData.getString(IMAGE_URI) ?: return Result.failure()
            Timber.i("ImageUri: $imageUri")

            fishRepository.uploadFishData(imageUri) { id, bitmap, progress ->
                startForegroundService(
                    id = id,
                    image = bitmap,
                    progress = progress
                )
            }

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
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setOnlyAlertOnce(true)
                    .setSmallIcon(R.drawable.ic_baseline_cloud_upload_24)
                    .setLargeIcon(image)
                    .setContentTitle(context.getString(R.string.uploading_fish_data))
                    .setProgress(100, progress, false)
                    .build()
            )
        )
    }

    companion object {
        const val GROUP_KEY = "me.siddheshkothadi.autofism3.FILE_UPLOAD"
        const val SUMMARY_ID = 0
        const val CHANNEL_ID = "upload_channel"
        const val IMAGE_URI = "IMAGE_URI"
        const val PROGRESS = "Progress"
    }
}