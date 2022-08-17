package me.siddheshkothadi.autofism3

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.media.Image.Plane
import android.view.Surface
import android.view.WindowManager
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.siddheshkothadi.autofism3.detection.env.ImageUtils
import me.siddheshkothadi.autofism3.detection.tflite.Classifier
import me.siddheshkothadi.autofism3.detection.tflite.DetectorFactory
import me.siddheshkothadi.autofism3.utils.fillBytes
import timber.log.Timber
import java.util.*

class FishAnalyzer(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val setResults: (Int, Int, MutableList<Classifier.Recognition>) -> Unit
) :
    ImageAnalysis.Analyzer {

    private val detector = DetectorFactory.getDetector(context.assets, MODEL_FILE_PATH)

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
//        detector.useGpu()
        val image = imageProxy.image

        if (image != null) {
            coroutineScope.launch(Dispatchers.IO) {
                val previewHeight = image.height
                val previewWidth = image.width

                Timber.i("$previewHeight, $previewWidth")

                val cropSize = detector.inputSize
                val sensorOrientation = ROTATION - getScreenOrientation()

                // Matrix to convert frame from 4_3 aspect ratio to cropped size and rotate
                val frameToCropTransform = ImageUtils.getTransformationMatrix(
                    previewWidth,
                    previewHeight,
                    cropSize,
                    cropSize,
                    sensorOrientation,
                    true
                )

                // Inverse of that matrix will convert cropped image to the actual 4_3 frame
//                val cropToFrameTransform = Matrix()
//                frameToCropTransform.invert(cropToFrameTransform)
//                val cropToFrameTransform = ImageUtils.getTransformationMatrix(
//                    cropSize,
//                    cropSize,
//                    previewHeight,
//                    previewWidth,
//                    0,
//                    true
//                )

                // yuv stores the og image info (4_3)
                val yuvBytes = arrayOfNulls<ByteArray>(3)
                val planes = image.planes
                fillBytes(planes, yuvBytes)
                val yRowStride = planes[0].rowStride
                val uvRowStride = planes[1].rowStride
                val uvPixelStride = planes[1].pixelStride

                // rgbBytes is 1d array that stores rgb values after converting yuv to rgb
                val rgbBytes = IntArray(previewWidth * previewHeight)

//                withContext(Dispatchers.IO) {
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
//                }

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

                val results: List<Classifier.Recognition> = detector.recognizeImage(croppedBitmap)
                Timber.i(results.toString())

                val minimumConfidence: Float =
                    MINIMUM_CONFIDENCE_TF_OD_API

                val mappedRecognitions: MutableList<Classifier.Recognition> =
                    LinkedList<Classifier.Recognition>()

//                val c2 = Canvas();

                for (result in results) {
                    val location = result.location
                    if (location != null && result.confidence >= minimumConfidence) {
//                        c2.drawRect(location, Paint())
//                        cropToFrameTransform.mapRect(location)
//                        result.location = location
                        mappedRecognitions.add(result)
                    }
                }

                Timber.i(mappedRecognitions.toString())
                setResults(croppedBitmap.height, croppedBitmap.width, mappedRecognitions)

                // ======================================================================

//                val bmp = coroutineScope.async(Dispatchers.IO) {
//                    ImageDetectionUtils.onImageAvailable(context, image)
//                        .rotate(imageProxy.imageInfo.rotationDegrees.toFloat())
//                }
//                Timber.i("${bmp.await().width}, ${bmp.await().height}");
//                val res = coroutineScope.async(Dispatchers.IO) {
//                    detector.recognizeImage(bmp.await())
//                }
//                Timber.i(res.await().toString())
                imageProxy.close()
            }

        } else {
            imageProxy.close()
        }
    }

    private fun getScreenOrientation(): Int {
        return when ((context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.orientation) {
            Surface.ROTATION_270 -> 270
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_90 -> 90
            else -> 0
        }
    }

    companion object {
        const val INPUT_SIZE = 640
        const val MODEL_FILE_PATH = "yolov5m_On5000.tflite"
        const val ROTATION = 90
        const val MINIMUM_CONFIDENCE_TF_OD_API = 0.8f
    }
}