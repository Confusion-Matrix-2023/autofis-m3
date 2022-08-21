package me.siddheshkothadi.autofism3.datastore


import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import me.siddheshkothadi.autofism3.datastore.Recognitions.getDefaultInstance
import java.io.InputStream
import java.io.OutputStream

object LocalDataSerializer : Serializer<LocalData> {
    override val defaultValue: LocalData
        get() = LocalData.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): LocalData {
        try {
            return LocalData.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: LocalData, output: OutputStream) {
        t.writeTo(output)
    }
}