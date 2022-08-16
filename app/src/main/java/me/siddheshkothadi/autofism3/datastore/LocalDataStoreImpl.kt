package me.siddheshkothadi.autofism3.datastore

import android.content.Context
import android.os.Build
import android.provider.Settings
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
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

class LocalDataStoreImpl @Inject constructor(
    private val context: FishApplication
) : LocalDataStore {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "device_data")
    private val deviceDataStore = context.dataStore
    private val deviceData: Flow<Preferences>
        get() = deviceDataStore.data

    override val deviceId: Flow<String>
        get() = deviceData.map { preferences ->
            preferences[DEVICE_ID_KEY] ?: ""
        }

    override val bearerToken: Flow<String>
        get() = deviceData.map { preferences ->
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

    private fun getDeviceName(): String {
        val deviceName =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) Settings.Global.getString(
                context.contentResolver,
                Settings.Global.DEVICE_NAME
            ) else Settings.Secure.getString(context.contentResolver, "bluetooth_name")

        return deviceName.replace("\\s+".toRegex(), "")
    }

    private fun generateDeviceId(): String {
        val deviceName = getDeviceName()
        val randomString = getRandomString()
        val timestamp = System.currentTimeMillis()
        val uniqueID = UUID.randomUUID().toString()

        return "$deviceName-$randomString-$timestamp-$uniqueID"
    }

    private fun getPersistentDeviceId(): String = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)

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

    override suspend fun setLocalData(): LocalData {
        val generatedDeviceId = getPersistentDeviceId()
        val jwt = generateJWT(generatedDeviceId)
        val generatedBearerToken = "Bearer $jwt"
        Timber.i(generatedBearerToken)
        setDeviceId(generatedDeviceId)
        setBearerToken(generatedBearerToken)
        return LocalData(generatedDeviceId, generatedBearerToken)
    }

    companion object {
        val DEVICE_ID_KEY = stringPreferencesKey("device_id")
        val BEARER_TOKEN_KEY = stringPreferencesKey("bearer_token")
        const val JWT_SECRET = "f00t4f30321@@0439!2#@am"
    }
}