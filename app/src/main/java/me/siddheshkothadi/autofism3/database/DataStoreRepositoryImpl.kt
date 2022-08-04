package me.siddheshkothadi.autofism3.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.siddheshkothadi.autofism3.FishApplication
import me.siddheshkothadi.autofism3.repository.DataStoreRepository
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt


class DataStoreRepositoryImpl @Inject constructor(
    context: FishApplication
) : DataStoreRepository
{
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "device_data")
    private val deviceDataStore = context.dataStore

    override val deviceData: Flow<Preferences>
        get() = deviceDataStore.data

    override val deviceId: Flow<String>
        get() = deviceDataStore.data.map { preferences ->
            preferences[DEVICE_ID_KEY] ?: ""
        }

    override val bearerToken: Flow<String>
        get() = deviceDataStore.data.map { preferences ->
            preferences[BEARER_TOKEN_KEY] ?: ""
        }

    private fun getRandomString(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        var string = ""
        for (i in (1..20)) {
            string += allowedChars[(Math.random() * (allowedChars.size - 1)).roundToInt()]
        }
        return string
    }

    private fun generateDeviceId(): String {
        val randomString = getRandomString()
        val timeStamp = System.currentTimeMillis()
        val uniqueID = UUID.randomUUID().toString()

        return "$randomString-$timeStamp-$uniqueID"
    }

    private fun generateJWT(generatedDeviceId: String): String {
        return Jwts.builder()
            .claim("device_id", generatedDeviceId)
            .signWith(SignatureAlgorithm.HS256, JWT_SECRET.toByteArray())
            .compact()
    }

    private suspend fun setDeviceId(generatedDeviceId: String) {
        deviceDataStore.edit { mutablePreferences ->
            mutablePreferences[DEVICE_ID_KEY] = generatedDeviceId
        }
    }

    private suspend fun setBearerToken(bearerToken: String) {
        deviceDataStore.edit { mutablePreferences ->
            mutablePreferences[BEARER_TOKEN_KEY] = bearerToken
        }
    }

    override suspend fun setDeviceId(): String {
        val generatedDeviceId = generateDeviceId()
        setDeviceId(generatedDeviceId)
        val jwt = generateJWT(generatedDeviceId)
        setBearerToken("Bearer $jwt")
        return generatedDeviceId
    }

    companion object {
        val DEVICE_ID_KEY = stringPreferencesKey("device_id")
        val BEARER_TOKEN_KEY = stringPreferencesKey("bearer_token")
        const val JWT_SECRET = "f00t4f30321@@0439!2#@am"
    }
}