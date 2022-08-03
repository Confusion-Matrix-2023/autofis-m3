package me.siddheshkothadi.autofism3.database

import android.content.Context
import android.provider.Settings
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.siddheshkothadi.autofism3.FishApplication
import me.siddheshkothadi.autofism3.repository.DataStoreRepository
import java.security.AccessController.getContext
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

class DataStoreRepositoryImpl @Inject constructor(
    context: FishApplication
) : DataStoreRepository
{
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "device_data")
    private val deviceDataStore = context.dataStore

    private fun getRandomString(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        var string = ""
        for (i in (1..20)) {
            string += allowedChars[(Math.random() * (allowedChars.size - 1)).roundToInt()]
        }
        return string
    }

    override val deviceData: Flow<Preferences>
        get() = deviceDataStore.data

    override val deviceId: Flow<String>
        get() = deviceDataStore.data.map { preferences ->
            preferences[DEVICE_ID_KEY] ?: ""
        }

    override fun generateDeviceId(): String {
        val randomString = getRandomString()
        val timeStamp = System.currentTimeMillis()
        val uniqueID = UUID.randomUUID().toString()

        return "$randomString-$timeStamp-$uniqueID"
    }

    override suspend fun setDeviceId(generatedDeviceId: String) {
        deviceDataStore.edit { mutablePreferences ->
            mutablePreferences[DEVICE_ID_KEY] = generatedDeviceId
        }
    }

    override suspend fun setDeviceId(): String {
        val generatedDeviceId = generateDeviceId()
        deviceDataStore.edit { mutablePreferences ->
            mutablePreferences[DEVICE_ID_KEY] = generatedDeviceId
        }
        return generatedDeviceId
    }

    companion object {
        val DEVICE_ID_KEY = stringPreferencesKey("device_id")
    }
}