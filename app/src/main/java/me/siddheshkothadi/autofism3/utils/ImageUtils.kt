package me.siddheshkothadi.autofism3.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.media.Image
import androidx.camera.core.ImageProxy
import me.siddheshkothadi.autofism3.detection.env.ImageUtils
import timber.log.Timber


fun ImageProxy.getBitmap(): Bitmap {
    val buffer = planes[0].buffer
    buffer.rewind()
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        .rotate(imageInfo.rotationDegrees.toFloat())
}

fun Bitmap.rotate(degrees: Float): Bitmap =
    Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply { postRotate(degrees) }, true)

fun Bitmap.getQualityNumber(): Int {
    return when (byteCount) {
        in 500001..800000 -> 15
        in 800001..1000000 -> 20
        in 1000001..1500000 -> 25
        in 1500001..2500000 -> 27
        in 2500001..3500000 -> 30
        in 3500001..4000000 -> 40
        in 4000001..5000000 -> 50
        else -> 75
    }
}

fun fillBytes(planes: Array<Image.Plane>, yuvBytes: Array<ByteArray?>) {
    // Because of the variable row stride it's not possible to know in
    // advance the actual necessary dimensions of the yuv planes.
    for (i in planes.indices) {
        val buffer = planes[i].buffer
        if (yuvBytes[i] == null) {
            yuvBytes[i] = ByteArray(buffer.capacity())
        }
        buffer[yuvBytes[i]]
    }
}