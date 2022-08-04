package me.siddheshkothadi.autofism3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.siddheshkothadi.autofism3.FishApplication
import me.siddheshkothadi.autofism3.repository.DataStoreRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
@ExperimentalPermissionsApi
class MainViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val context: FishApplication
) : ViewModel() {
    private val _deviceId = MutableStateFlow<String?>("")
    val deviceId = _deviceId.asStateFlow()

    init {
        viewModelScope.launch {
            val dataStoreDeviceId = dataStoreRepository.deviceId.first()
            if(dataStoreDeviceId.isBlank()) {
                Timber.i("Device Id Not Found! Generating new device id...")
                _deviceId.value = dataStoreRepository.setDeviceId()
            } else {
                _deviceId.value = dataStoreDeviceId
            }
            Timber.i("Device ID: ${deviceId.value}")
        }
    }
}