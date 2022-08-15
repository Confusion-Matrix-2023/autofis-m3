package me.siddheshkothadi.autofism3

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import coil.compose.AsyncImage
import me.siddheshkothadi.autofism3.ui.theme.AutoFISM3Theme

class TestActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current
            val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
            var camera by remember { mutableStateOf<Camera?>(null) }
            var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

            var bitmap: Bitmap? by remember {
                mutableStateOf(null)
            }

            AutoFISM3Theme {
                Box(
                    modifier = Modifier.fillMaxSize(),
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
                                            FishAnalyzer() { bmp ->
                                                bitmap = bmp
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
                        modifier = Modifier.fillMaxSize()
                    )
                    if(bitmap !=  null) {
                        AsyncImage(model = bitmap, contentDescription = "Frame Image")
                    }
                }
            }
        }
    }
}