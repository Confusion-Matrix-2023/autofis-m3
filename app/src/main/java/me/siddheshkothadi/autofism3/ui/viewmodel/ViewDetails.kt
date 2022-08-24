package me.siddheshkothadi.autofism3.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.siddheshkothadi.autofism3.model.PendingUploadFish
import me.siddheshkothadi.autofism3.repository.FishRepository

class ViewDetailsViewModel constructor(
    private val fishRepository: FishRepository
) : ViewModel() {
    suspend fun getFishData(imageUri: String): PendingUploadFish {
        return fishRepository.getPendingUploadByImageUri(imageUri)
    }
}