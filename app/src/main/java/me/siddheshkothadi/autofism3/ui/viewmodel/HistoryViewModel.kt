package me.siddheshkothadi.autofism3.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.siddheshkothadi.autofism3.FishApplication
import me.siddheshkothadi.autofism3.model.PendingUploadFish
import me.siddheshkothadi.autofism3.model.UploadHistoryFish
import me.siddheshkothadi.autofism3.repository.FishRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val fishRepository: FishRepository,
) : ViewModel() {

    private val _isFetching = mutableStateOf(false);
    val isFetching = _isFetching

    val pendingUploads: Flow<List<PendingUploadFish>>
        get() = fishRepository.getPendingUploads()

    val uploadHistory: Flow<List<UploadHistoryFish>>
        get() = fishRepository.getUploadHistory()

    init {
        fetchUploadHistory()
    }

    fun fetchUploadHistory() {
        viewModelScope.launch {
            try {
                _isFetching.value = true
                withContext(Dispatchers.IO) {
                    fishRepository.fetchUploadHistory()
                }
            } catch (exception: Exception) {
                Timber.e(exception)
            } finally {
                _isFetching.value = false
            }
        }
    }
}