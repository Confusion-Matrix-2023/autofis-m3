package me.siddheshkothadi.autofism3.datastore

import android.content.Context
import android.graphics.Bitmap
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import me.siddheshkothadi.autofism3.detection.tflite.Classifier

interface LocalDataStore {
    val id: Flow<String>
    val deviceName: Flow<String>
    val localData: Flow<LocalData>
    val deviceKey: Flow<String>
    val bearerToken: Flow<String>
    val recognitions: Flow<Recognitions>
    val bitmapInfo: Flow<BitmapInfo>

    fun generateJWT(generatedDeviceId: String): String

    suspend fun setDeviceKeyNameAndBearerToken()
    suspend fun setId(id: String)
    suspend fun setDeviceKey(generatedDeviceId: String)
    suspend fun setBearerToken(bearerToken: String)
    suspend fun setDeviceName(name: String)
    suspend fun setBitmap(bitmap: Bitmap)
    suspend fun setRecognitions(list: List<Classifier.Recognition>)
}