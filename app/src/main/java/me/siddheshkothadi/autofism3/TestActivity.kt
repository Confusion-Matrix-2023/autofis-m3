package me.siddheshkothadi.autofism3

import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
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
import kotlin.math.min
import kotlin.properties.Delegates

class TestActivity : ComponentActivity() {
    private var textSizePx by Delegates.notNull<Float>();

    private val paintConfig = Paint().apply {
        color = Color.BLUE
        strokeWidth = 10.0f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        strokeMiter = 100f
    }

    private lateinit var interiorPaint: Paint
    private lateinit var exteriorPaint: Paint

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen()
        super.onCreate(savedInstanceState)

        textSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            15f,
            this.resources.displayMetrics
        )

        interiorPaint = Paint().apply {
            textSize = textSizePx
            color = Color.WHITE
            style = Paint.Style.FILL
            isAntiAlias = false
            alpha = 255
        }

        exteriorPaint = Paint().apply {
            textSize = textSizePx
            color = Color.BLUE
            style = Paint.Style.FILL
            isAntiAlias = false
            alpha = 255
        }

        setContent {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            val lifecycleOwner = LocalLifecycleOwner.current
            val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
            var camera by remember { mutableStateOf<Camera?>(null) }
            var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

//            var bitmap: Bitmap? by remember {
//                mutableStateOf(null)
//            }
            var bitmapHeight: Int by remember {
                mutableStateOf(0)
            }
            var bitmapWidth: Int by remember {
                mutableStateOf(0)
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
                            .fillMaxSize()
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
                                    (posX + width.toInt()* scaleX) * scaleX,
                                    posY* scaleY,
                                    paint
                                )

                                drawText(labelString, posX*scaleX, (posY + textSize.toInt() -10)*scaleY, interiorPaint)
                            }
                        }
                    }

                }
            }
        }
    }
}
