package me.siddheshkothadi.autofism3.ui.viewmodel

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import me.siddheshkothadi.autofism3.FishApplication
import me.siddheshkothadi.autofism3.model.PendingUploadFish
import me.siddheshkothadi.autofism3.repository.FishRepository
import me.siddheshkothadi.autofism3.utils.DateUtils
import me.siddheshkothadi.autofism3.workmanager.UploadWorker
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ExperimentalPermissionsApi
@SuppressLint("MissingPermission")
@HiltViewModel
class EnterDetailsViewModel @Inject constructor(
    private val fishRepository: FishRepository,
    app: FishApplication,
) : ViewModel() {
    private val _isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _quantity: MutableState<String> = mutableStateOf("")
    val quantity: State<String> = _quantity

    private val _latitude: MutableState<String> = mutableStateOf("")
    val latitude: State<String> = _latitude

    private val _longitude: MutableState<String> = mutableStateOf("")
    val longitude: State<String> = _longitude

    private val _timestamp = MutableStateFlow<String>("")
    private val timestamp: StateFlow<String> = _timestamp

    @OptIn(ExperimentalCoroutinesApi::class)
    val dateString = _timestamp.mapLatest { timestampString ->
        DateUtils.getDate(timestampString)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val timeString = _timestamp.mapLatest { timestampString ->
        DateUtils.getTime(timestampString)
    }

    private val workManager = WorkManager.getInstance(app)

    fun enqueueDataUploadRequest(uri: String) {
        val uploadRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED
                    )
                    .build()
            )
            .setInputData(
                workDataOf(
                    "IMAGE_URI" to uri
                )
            )
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
        workManager.enqueue(uploadRequest)
    }

    init {
        _isLoading.value = true
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(app)
        viewModelScope.launch {
            _timestamp.value = System.currentTimeMillis().toString()
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    _latitude.value = location?.latitude.toString()
                    _longitude.value = location?.longitude.toString()
                    Timber.i("$latitude, $longitude")
                }
                .addOnFailureListener { e ->
                    Timber.e(e)
                }
                .addOnCompleteListener {
                    Timber.i("Complete")
                    _isLoading.value = false
                }
        }
    }

    fun setQuantity(q: String) {
        _quantity.value = q
    }

    suspend fun insertData(imageUri: String) {
        val fish = PendingUploadFish(
            imageUri = imageUri,
            longitude = longitude.value,
            latitude = latitude.value,
            quantity = quantity.value,
            timestamp = timestamp.value,
        )

        Timber.i(fish.toString())

        fishRepository.insertFish(fish)
    }
}