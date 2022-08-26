package me.siddheshkothadi.autofism3.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.net.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.siddheshkothadi.autofism3.FishApplication
import me.siddheshkothadi.autofism3.datastore.BitmapInfo
import me.siddheshkothadi.autofism3.model.PendingUploadFish
import me.siddheshkothadi.autofism3.repository.FishRepository
import me.siddheshkothadi.autofism3.utils.DateUtils
import me.siddheshkothadi.autofism3.utils.awaitCurrentLocation
import me.siddheshkothadi.autofism3.utils.toCelsius
import timber.log.Timber
import javax.inject.Inject


@ExperimentalPermissionsApi
@SuppressLint("MissingPermission")
@HiltViewModel
class EnterDetailsViewModel @Inject constructor(
    private val fishRepository: FishRepository,
    private val app: FishApplication,
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

    val isConnectedToNetwork = mutableStateOf(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val dateString = _timestamp.mapLatest { timestampString ->
        DateUtils.getDate(app, timestampString)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val timeString = _timestamp.mapLatest { timestampString ->
        DateUtils.getTime(app, timestampString)
    }

    val boundingBoxes: Flow<List<RectF>> = fishRepository.boundingBoxes
    val bitmapInfo: Flow<BitmapInfo> = fishRepository.bitmapInfo
    val selectedBox = mutableStateOf(0)

    val temp: MutableState<String?> = mutableStateOf(null)
    val pressure: MutableState<String?> = mutableStateOf(null)
    val humidity: MutableState<String?> = mutableStateOf(null)
    val speed: MutableState<String?> = mutableStateOf(null)
    val deg: MutableState<String?> = mutableStateOf(null)

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            isConnectedToNetwork.value = true
        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            isConnectedToNetwork.value = false
        }
    }

    init {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                withContext(Dispatchers.IO) {
                    _timestamp.value = System.currentTimeMillis().toString()
                    fetchLocation()
                    val connectivityManager = getSystemService(app, ConnectivityManager::class.java) as ConnectivityManager
                    connectivityManager.requestNetwork(networkRequest, networkCallback)
                    val res = fishRepository.getWeatherData(latitude.value, longitude.value)
                    temp.value = res.get("main").asJsonObject.get("temp").toString().toCelsius()
                    pressure.value = res.get("main").asJsonObject.get("pressure").toString()
                    humidity.value = res.get("main").asJsonObject.get("humidity").toString()
                    speed.value = res.get("wind").asJsonObject.get("speed").toString()
                    deg.value = res.get("wind").asJsonObject.get("deg").toString()

                    Timber.i("$temp $pressure $humidity $speed $deg")
                    Timber.i(res.toString())
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
            _isLoading.value = false
        }
    }

    fun checkLocationAccess(activity: Activity) {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = (10000 / 2).toLong()

        val locationSettingsRequestBuilder = LocationSettingsRequest.Builder()

        locationSettingsRequestBuilder.addLocationRequest(locationRequest)
        locationSettingsRequestBuilder.setAlwaysShow(true)

        val settingsClient = LocationServices.getSettingsClient(activity)
        val task: Task<LocationSettingsResponse> =
            settingsClient.checkLocationSettings(locationSettingsRequestBuilder.build())

        task.addOnSuccessListener {
            Timber.i("Success $it")
        }

        task.addOnFailureListener {
            try {
                Timber.i("Failure")
                val resolvableApiException = it as ResolvableApiException
                resolvableApiException.startResolutionForResult(
                    activity,
                    0x1
                )
            } catch (sendIntentException: SendIntentException) {
                sendIntentException.printStackTrace()
            }
        }
    }

    private suspend fun fetchLocation() {
        val location = fusedLocationClient.awaitCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY)
        location?.let {
            _latitude.value = it.latitude.toString()
            _longitude.value = it.longitude.toString()
        }
        Timber.i("$latitude, $longitude")
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
                    quantity = quantity.value,
                    temp = temp.value,
                    humidity = humidity.value,
                    pressure = pressure.value,
                    speed = speed.value,
                    deg = deg.value
                )

                fishRepository.enqueueUpload(fish)
            }
        }
    }
}