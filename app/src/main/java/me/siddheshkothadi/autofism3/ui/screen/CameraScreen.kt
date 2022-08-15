package me.siddheshkothadi.autofism3.ui.screen

import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.siddheshkothadi.autofism3.CustomImageAnalyzer
import me.siddheshkothadi.autofism3.utils.DateUtils
import me.siddheshkothadi.autofism3.utils.getBitmap
import me.siddheshkothadi.autofism3.utils.getUri
import me.siddheshkothadi.autofism3.utils.storeBitmap
import timber.log.Timber
import java.io.File
import java.net.URLEncoder
import kotlin.random.Random

@Composable
fun CameraScreen(
    navController: NavHostController
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var isFlashOn by remember { mutableStateOf(false) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var imageUri: String by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    var h by remember { mutableStateOf(0f) }
    var w by remember { mutableStateOf(0f) }

    var top by remember { mutableStateOf(0f) }
    var bottom by remember { mutableStateOf(0f) }
    var left by remember { mutableStateOf(0f) }
    var right by remember { mutableStateOf(0f) }

    var detectedLabel by remember { mutableStateOf("") }
    var accuracyText by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets.statusBars.only(
                    WindowInsetsSides.Top
                )
            ),
        color = MaterialTheme.colorScheme.background
    ) {
        Box {
            Box {
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
                                        CustomImageAnalyzer { rect, label, imageHeight, imageWidth ->
                                            top = rect.top * 1f
                                            bottom = rect.bottom * 1f
                                            left = rect.left * 1f
                                            right = rect.right * 1f

                                            h = imageHeight * 1f
                                            w = imageWidth * 1f

                                            detectedLabel = label.text
                                            accuracyText = label.confidence.toString()
                                        })
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
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val scaleY = size.height * 1f / w
                    val scaleX = size.width * 1f / h
                    drawContext.canvas.nativeCanvas.apply {
                        drawRect(
                            RectF(
                                left * scaleX,
                                top * scaleY,
                                right * scaleX,
                                bottom * scaleY
                            ),
                            Paint().apply {
                                color = Color.WHITE
                                strokeWidth = 8F
                                style = Paint.Style.STROKE
                            }
                        )
                        drawText(
                            detectedLabel,
                            left * scaleX + 256f,
                            top * scaleY - 80f,
                            Paint().apply {
                                textSize = 48f
                                color = Color.WHITE
                                textAlign = Paint.Align.CENTER
                            }
                        )
                        drawText(
                            accuracyText,
                            left * scaleX + 256f,
                            top * scaleY - 20f,
                            Paint().apply {
                                textSize = 50f
                                color = Color.WHITE
                                textAlign = Paint.Align.CENTER
                            }
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (camera != null) {
                        if (isFlashOn) {
                            if (camera!!.cameraInfo.hasFlashUnit()) {
                                camera!!.cameraControl.enableTorch(false)
                                isFlashOn = false
                            }
                        } else {
                            if (camera!!.cameraInfo.hasFlashUnit()) {
                                camera!!.cameraControl.enableTorch(true)
                                isFlashOn = true
                            }
                        }
                    }
                }) {
                    if (isFlashOn) Icon(
                        Icons.Filled.FlashOn,
                        "", tint = androidx.compose.ui.graphics.Color.White
                    ) else Icon(
                        Icons.Filled.FlashOff,
                        "",
                        tint = androidx.compose.ui.graphics.Color.White
                    )
                }
                Text(
                    "AutoFIS",
                    style = MaterialTheme.typography.titleLarge,
                    color = androidx.compose.ui.graphics.Color.White
                )
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.MoreVert, "", tint = androidx.compose.ui.graphics.Color.White)
                }
            }

            ElevatedButton(
                modifier = Modifier
                    .padding(12.dp)
                    .size(64.dp)
                    .align(Alignment.BottomCenter),
                onClick = {
                    Timber.i("Clicked at ${DateUtils.getTimeSec(System.currentTimeMillis())}")
                    coroutineScope.launch {
                        isLoading = true
                        imageCapture?.takePicture(
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageCapturedCallback() {
                                @ExperimentalGetImage
                                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                    coroutineScope.launch {
                                        try {
                                            isLoading = true
                                            withContext(Dispatchers.IO) {
                                                // TODO: Below line consumes time (>1sec)
                                                val bitmap = imageProxy.getBitmap()
                                                val randomInt = Random.nextInt()
                                                val imageFile = File(context.filesDir, "fish_image_${System.currentTimeMillis()}_${randomInt}.jpg")
                                                imageFile.storeBitmap(context, bitmap)
                                                imageUri = URLEncoder.encode(imageFile.getUri(context).toString(), "utf-8")
                                                Timber.i("Done at ${DateUtils.getTimeSec(System.currentTimeMillis())}")
                                            }
                                        } catch(exception: Exception) {
                                            Timber.e(exception)
                                        } finally {
                                            if(imageUri.isNotBlank()) {
                                                navController.navigate("enter-details/$imageUri")
                                            }
                                            isLoading = false
                                            imageProxy.close()
                                        }
                                    }
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    Timber.tag("Image Capture").e(exception.toString())
                                    isLoading = false
                                }
                            }
                        )
                    }
                },
                enabled = !isLoading,
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Filled.CameraAlt, "Camera")
            }

            if (isLoading) {
                CircularProgressIndicator(
                    Modifier.align(Alignment.Center),
                    color = androidx.compose.ui.graphics.Color(0xffffffff)
                )
            }
        }
    }
}