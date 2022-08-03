package me.siddheshkothadi.autofism3.repository

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    // For all data
    val deviceData: Flow<Preferences>

    // for device id
    val deviceId: Flow<String>
    fun generateDeviceId(): String
    suspend fun setDeviceId(generatedDeviceId: String)
    suspend fun setDeviceId(): String
}