package me.siddheshkothadi.autofism3.datastore

import android.content.Context
import android.graphics.Bitmap
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import me.siddheshkothadi.autofism3.detection.tflite.Classifier

interface LocalDataStore {
    val localData: Flow<LocalData>
    val deviceId: Flow<String>
    val bearerToken: Flow<String>
    val recognitions: Flow<Recognitions>
    val bitmapInfo: Flow<BitmapInfo>

    suspend fun setDeviceIdAndBearerToken()
    suspend fun setBitmap(bitmap: Bitmap)
    suspend fun setRecognitions(list: List<Classifier.Recognition>)
}