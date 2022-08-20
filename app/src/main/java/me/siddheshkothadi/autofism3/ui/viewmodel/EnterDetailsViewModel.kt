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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        DateUtils.getDate(app, timestampString)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val timeString = _timestamp.mapLatest { timestampString ->
        DateUtils.getTime(app, timestampString)
    }

    init {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                viewModelScope.launch(Dispatchers.IO) {
                    _timestamp.value = System.currentTimeMillis().toString()
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(app)
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            // Got last known location. In some rare situations this can be null.
                            _latitude.value = if(location?.latitude == null) {
                                ""
                            } else {
                                location.latitude.toString()
                            }
                            _longitude.value = if(location?.longitude == null) {
                                ""
                            } else {
                                location.longitude.toString()
                            }
                            Timber.i("$latitude, $longitude")
                        }
                        .addOnFailureListener { e ->
                            Timber.e(e)
                        }
                }
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setQuantity(q: String) {
        _quantity.value = q
    }

    fun enqueueDataUploadRequest(imageUri: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val fish = PendingUploadFish(
                    imageUri = imageUri,
                    timestamp = timestamp.value,
                    longitude = longitude.value,
                    latitude = latitude.value,
                    quantity = quantity.value
                )

                fishRepository.enqueueUpload(fish)
            }
        }
    }
}