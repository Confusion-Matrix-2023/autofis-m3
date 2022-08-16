package me.siddheshkothadi.autofism3

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import me.siddheshkothadi.autofism3.detection.tflite.Classifier
import me.siddheshkothadi.autofism3.ui.theme.AutoFISM3Theme
import timber.log.Timber

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            val lifecycleOwner = LocalLifecycleOwner.current
            val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
            var camera by remember { mutableStateOf<Camera?>(null) }
            var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

            var bitmap: Bitmap? by remember {
                mutableStateOf(null)
            }

            var results: List<Classifier.Recognition> by remember {
                mutableStateOf(listOf())
            }

            AutoFISM3Theme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                        .windowInsetsPadding(
                            WindowInsets.statusBars.only(
                                WindowInsetsSides.Top
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val executor = ContextCompat.getMainExecutor(ctx)
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder()
                                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                                    .build()
                                    .apply {
                                        setSurfaceProvider(previewView.surfaceProvider)
                                    }

                                val cameraSelector = CameraSelector.Builder()
                                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                    .build()

                                imageCapture = ImageCapture.Builder()
                                    .setTargetRotation(previewView.display.rotation)
                                    .build()

                                val imageAnalysis = ImageAnalysis.Builder()
                                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()
                                    .apply {
                                        setAnalyzer(
                                            executor,
                                            FishAnalyzer(context, coroutineScope) { bmp, r ->
                                                bitmap = bmp
                                                results = r
                                            }
                                        )
                                    }

                                cameraProvider.unbindAll()
                                camera = cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    imageAnalysis,
                                    imageCapture,
                                    preview
                                )
                            }, executor)
                            previewView
                        },
                        modifier = Modifier.aspectRatio(1f).fillMaxSize()
                    )
                    Canvas(modifier = Modifier.aspectRatio(1f).fillMaxSize()) {
                        if (bitmap != null) {
                            val scaleY = size.height * 1f / bitmap!!.width
                            val scaleX = size.width * 1f / bitmap!!.height
                            results.forEach {
                                Timber.i(it.toString())
                                drawContext.canvas.nativeCanvas.apply {
                                    drawRect(
                                        RectF(
                                            it.location.left * scaleX,
                                            it.location.top * scaleY,
                                            it.location.right * scaleX,
                                            it.location.bottom * scaleY
                                        ),
                                        Paint().apply {
                                            color = Color.WHITE
                                            strokeWidth = 8F
                                            style = Paint.Style.STROKE
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
