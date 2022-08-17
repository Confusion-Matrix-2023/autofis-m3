package me.siddheshkothadi.autofism3.ui.screen

import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextUtils
import android.util.TypedValue
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
import me.siddheshkothadi.autofism3.FishAnalyzer
import me.siddheshkothadi.autofism3.detection.tflite.Classifier
import me.siddheshkothadi.autofism3.utils.DateUtils
import me.siddheshkothadi.autofism3.utils.getBitmap
import me.siddheshkothadi.autofism3.utils.getUri
import me.siddheshkothadi.autofism3.utils.storeBitmap
import timber.log.Timber
import java.io.File
import java.net.URLEncoder
import kotlin.math.min
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

    var bitmapHeight: Int by remember {
        mutableStateOf(0)
    }
    var bitmapWidth: Int by remember {
        mutableStateOf(0)
    }

    var results: List<Classifier.Recognition> by remember {
        mutableStateOf(listOf())
    }

    val paintConfig = remember {
        Paint().apply {
            color = Color.BLUE
            strokeWidth = 10.0f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            strokeMiter = 100f
        }
    }

    val textSizePx = remember {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            15f,
            context.resources.displayMetrics
        )
    }

    val interiorPaint = remember {
        Paint().apply {
            textSize = textSizePx
            color = Color.WHITE
            style = Paint.Style.FILL
            isAntiAlias = false
            alpha = 255
        }
    }

    val exteriorPaint = remember {
        Paint().apply {
            textSize = textSizePx
            color = Color.BLUE
            style = Paint.Style.FILL
            isAntiAlias = false
            alpha = 255
        }
    }

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
                                        FishAnalyzer(context, coroutineScope) { h, w, r ->
                                            bitmapHeight = h
                                            bitmapWidth = w
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
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxSize(),
                )
                Canvas(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxSize()
                ) {
                    val scaleY = size.height * 1f / bitmapWidth
                    val scaleX = size.width * 1f / bitmapHeight
                    results.forEach {
                        val cornerSize: Float =
                            min(it.location.width(), it.location.height()) / 8.0f
                        val width = exteriorPaint.measureText(it.title)
                        val textSize = exteriorPaint.textSize
                        val paint = Paint(paintConfig)
                        paint.style = Paint.Style.FILL
                        paint.alpha = 160
                        val posX = it.location.left + cornerSize
                        val posY = it.location.top

                        val labelString = if (!TextUtils.isEmpty(it.title)) String.format(
                            "%s %.2f", it.title,
                            100 * it.confidence
                        ) else String.format("%.2f", 100 * it.confidence)

                        drawContext.canvas.nativeCanvas.apply {
                            drawRoundRect(
                                RectF(
                                    it.location.left * scaleX,
                                    it.location.top * scaleY,
                                    it.location.right * scaleX,
                                    it.location.bottom * scaleY
                                ),
                                cornerSize,
                                cornerSize,
                                paintConfig
                            )
                            drawRect(
                                posX * scaleX,
                                (posY + textSize.toInt()) * scaleY,
                                (posX + width.toInt() * scaleX) * scaleX,
                                posY * scaleY,
                                paint
                            )

                            drawText(
                                labelString,
                                posX * scaleX,
                                (posY + textSize.toInt() - 10) * scaleY,
                                interiorPaint
                            )
                        }
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
                        ""
                    ) else Icon(
                        Icons.Filled.FlashOff,
                        "",
                    )
                }
                Text(
                    "AutoFIS",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.MoreVert, "")
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
                                        withContext(Dispatchers.IO) {
                                            Timber.i(
                                                "Received ImageProxy at ${
                                                    DateUtils.getTimeSec(
                                                        System.currentTimeMillis()
                                                    )
                                                }"
                                            )
                                            isLoading = true

                                            // TODO: Below line consumes time (>1sec)
                                            val bitmap = imageProxy.getBitmap()

                                            imageProxy.close()
                                            val randomInt = Random.nextInt()
                                            val imageFile = File(
                                                context.filesDir,
                                                "fish_image_${System.currentTimeMillis()}_${randomInt}.jpg"
                                            )
                                            imageFile.storeBitmap(context, bitmap)
                                            imageUri = URLEncoder.encode(
                                                imageFile.getUri(context).toString(), "utf-8"
                                            )
                                            Timber.i("Done at ${DateUtils.getTimeSec(System.currentTimeMillis())}")
                                            isLoading = false

                                            launch(Dispatchers.Main) {
                                                if (imageUri.isNotBlank()) {
                                                    navController.navigate("enter-details/$imageUri")
                                                }
                                            }
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
                enabled = !(isLoading || results.isEmpty()),
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