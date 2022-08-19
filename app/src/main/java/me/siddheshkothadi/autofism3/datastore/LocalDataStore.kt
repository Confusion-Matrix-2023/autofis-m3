package me.siddheshkothadi.autofism3.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import java.util.*

interface LocalDataStore {
    val deviceId: Flow<String>
    val bearerToken: Flow<String>

    suspend fun setDeviceIdAndBearerToken()
}