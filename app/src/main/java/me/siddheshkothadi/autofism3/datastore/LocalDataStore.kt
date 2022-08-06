package me.siddheshkothadi.autofism3.datastore

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface LocalDataStore {
    val deviceId: Flow<String>
    val bearerToken: Flow<String>

    suspend fun setLocalData(): LocalData
}