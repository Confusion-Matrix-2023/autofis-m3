package me.siddheshkothadi.autofism3

import android.annotation.SuppressLint
import android.graphics.Rect
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import timber.log.Timber

class CustomImageAnalyzer(val drawBoundingBox: (rect: android.graphics.Rect, dLabel: DetectedObject.Label, imageHeight: Int, imageWidth: Int) -> Unit) :
    ImageAnalysis.Analyzer {

    val localModel = LocalModel.Builder()
        .setAssetFilePath("SeeFish_1_0.tflite")
        .build()

    val options =
        CustomObjectDetectorOptions.Builder(localModel)
            .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
            .enableClassification()
            .setClassificationConfidenceThreshold(0.7f)
            .setMaxPerObjectLabelCount(1)
            .build()

    val objectDetector = ObjectDetection.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            objectDetector.process(image)
                .addOnSuccessListener { detectedObjects ->
                    if(detectedObjects.isNotEmpty()) {
                        for (detectedObject in detectedObjects) {
                            val boundingBox = detectedObject.boundingBox
                            val trackingId = detectedObject.trackingId
                            if (detectedObject.labels.isNotEmpty()) {
                                for (label in detectedObject.labels) {
                                    drawBoundingBox(boundingBox, label, image.height, image.width)
                                    Timber.tag("Res")
                                        .i(label.text + " " + label.confidence + " " + label.index)
                                }
                            } else {
                                // TODO: Clear canvas
                                drawBoundingBox(Rect(0,0,0,0), DetectedObject.Label("", 0f, 0), 0, 0)
                            }
                        }
                    }
                    else {
                        drawBoundingBox(Rect(0,0,0,0), DetectedObject.Label("", 0f, 0), 0, 0)
                    }
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    // ...
                    Timber.i(e.toString())
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}