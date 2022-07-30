package me.siddheshkothadi.autofism3.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun File.getUri(context: Context): Uri? {
    return FileProvider.getUriForFile(
        context,
        context.packageName + ".provider",
        this
    )
}

fun File.storeBitmap(context: Context, bitmap: Bitmap) {
    getUri(context)?.run {
        context.contentResolver.openOutputStream(this)?.run {
            bitmap.compress(Bitmap.CompressFormat.JPEG, bitmap.getQualityNumber(), this)
            flush()
            close()
        }
    }
}
