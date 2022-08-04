package me.siddheshkothadi.autofism3.repository

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    val deviceData: Flow<Preferences>
    val deviceId: Flow<String>
    val bearerToken: Flow<String>

    suspend fun setDeviceId(): String
}