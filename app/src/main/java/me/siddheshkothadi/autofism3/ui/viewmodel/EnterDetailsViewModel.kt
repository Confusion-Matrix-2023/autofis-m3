package me.siddheshkothadi.autofism3.ui.viewmodel

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.siddheshkothadi.autofism3.FishApplication
import me.siddheshkothadi.autofism3.model.PendingUploadFish
import me.siddheshkothadi.autofism3.repository.FishRepository
import me.siddheshkothadi.autofism3.utils.DateUtils
import me.siddheshkothadi.autofism3.utils.awaitCurrentLocation
import timber.log.Timber
import javax.inject.Inject

@ExperimentalPermissionsApi
@SuppressLint("MissingPermission")
@HiltViewModel
class EnterDetailsViewModel @Inject constructor(
    private val fishRepository: FishRepository,
    app: FishApplication,
) : ViewModel() {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(app)

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
                withContext(Dispatchers.IO) {
                    _timestamp.value = System.currentTimeMillis().toString()
                    val location = fusedLocationClient.awaitCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    location?.let {
                        _latitude.value = it.latitude.toString()
                        _longitude.value = it.longitude.toString()
                    }
                    Timber.i("$latitude, $longitude")
                }
            } catch (e: Exception) {
                Timber.e(e)
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