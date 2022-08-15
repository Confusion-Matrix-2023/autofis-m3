package me.siddheshkothadi.autofism3

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import me.siddheshkothadi.autofism3.utils.getBitmap
import timber.log.Timber

class FishAnalyzer(private val setBitmap: (Bitmap) -> Unit) : ImageAnalysis.Analyzer {

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val image = imageProxy.image

        if(image != null) {
            val bitmap = imageProxy.getBitmap()
            setBitmap(bitmap)
            Timber.i(image.toString())
        }

        imageProxy.close()
    }
}