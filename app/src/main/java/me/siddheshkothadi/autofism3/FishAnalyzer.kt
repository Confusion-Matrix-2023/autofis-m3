package me.siddheshkothadi.autofism3

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import me.siddheshkothadi.autofism3.detection.env.ImageUtils
import me.siddheshkothadi.autofism3.detection.tflite.YoloV5Classifier
import me.siddheshkothadi.autofism3.utils.fillBytes

class FishAnalyzer(
    private val coroutineScope: CoroutineScope,
    private val detector: YoloV5Classifier,
    private val setResults: (Bitmap) -> Unit
) :
    ImageAnalysis.Analyzer {

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val image = imageProxy.image

        if (image != null) {
            coroutineScope.launch(Dispatchers.IO) {
                val previewHeight = image.height
                val previewWidth = image.width

                val cropSize = detector.inputSize
                val sensorOrientation = imageProxy.imageInfo.rotationDegrees

                // Matrix to convert frame from 4_3 aspect ratio to cropped size and rotate
                val frameToCropTransform = ImageUtils.getTransformationMatrix(
                    previewWidth,
                    previewHeight,
                    cropSize,
                    cropSize,
                    sensorOrientation,
                    true
                )

                // yuv stores the og image info (4_3)
                val yuvBytes = arrayOfNulls<ByteArray>(3)
                val planes = image.planes
                fillBytes(planes, yuvBytes)
                val yRowStride = planes[0].rowStride
                val uvRowStride = planes[1].rowStride
                val uvPixelStride = planes[1].pixelStride

                // rgbBytes is 1d array that stores rgb values after converting yuv to rgb
                val rgbBytes = IntArray(previewWidth * previewHeight)

                ImageUtils.convertYUV420ToARGB8888(
                    yuvBytes[0],
                    yuvBytes[1],
                    yuvBytes[2],
                    previewWidth,
                    previewHeight,
                    yRowStride,
                    uvRowStride,
                    uvPixelStride,
                    rgbBytes
                )

                // We set rgbBytes values in rgbFrameBitmap
                val rgbFrameBitmap =
                    Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888)
                rgbFrameBitmap.setPixels(
                    rgbBytes,
                    0,
                    previewWidth,
                    0,
                    0,
                    previewWidth,
                    previewHeight
                )

                // Save cropped and transformed (rotated) image in croppedBitmap
                val croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(croppedBitmap)
                canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null)

                setResults(croppedBitmap)
                imageProxy.close()
            }
        }
        else {
            imageProxy.close()
        }
    }
}