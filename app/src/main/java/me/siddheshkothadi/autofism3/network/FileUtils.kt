package me.siddheshkothadi.autofism3.network

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source

fun ContentResolver.readAsRequestBody(uri: Uri): RequestBody =
    object: RequestBody() {
        override fun contentType(): MediaType? =
            this@readAsRequestBody.getType(uri)?.toMediaTypeOrNull()

        override fun writeTo(sink: BufferedSink) {
            this@readAsRequestBody.openInputStream(uri)?.source()?.use(sink::writeAll)
        }

        override fun contentLength(): Long =
            this@readAsRequestBody.query(uri, null, null, null, null)?.use { cursor ->
                val sizeColumnIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
                cursor.moveToFirst()
                cursor.getLong(sizeColumnIndex)
            } ?: super.contentLength()
    }