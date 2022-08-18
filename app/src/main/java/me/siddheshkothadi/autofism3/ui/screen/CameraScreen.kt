package me.siddheshkothadi.autofism3.ui.screen

import android.graphics.Bitmap
import android.util.Rational
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
import me.siddheshkothadi.autofism3.FishAnalyzer
import me.siddheshkothadi.autofism3.MainViewModel
import me.siddheshkothadi.autofism3.detection.Constants
import me.siddheshkothadi.autofism3.detection.tflite.Classifier
import me.siddheshkothadi.autofism3.detection.tflite.YoloV5Classifier
import me.siddheshkothadi.autofism3.utils.DateUtils
import me.siddheshkothadi.autofism3.utils.getBitmap
import me.siddheshkothadi.autofism3.utils.getUri
import me.siddheshkothadi.autofism3.utils.storeBitmap
import timber.log.Timber
import java.io.File
import java.net.URLEncoder
import java.util.*
import kotlin.math.min
import kotlin.random.Random

@Composable
fun CameraScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel
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
    var dialogText by remember { mutableStateOf("") }

    var bitmap: Bitmap? by remember {
        mutableStateOf(null)
    }

    val fishAnalyzer = remember {
        mainViewModel.detector?.let {
            FishAnalyzer(coroutineScope, it) { bmp ->
                bitmap = bmp
            }
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
                                    if (fishAnalyzer != null) {
                                        setAnalyzer(
                                            executor,
                                            fishAnalyzer
                                        )
                                    }
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
                        if (bitmap != null && mainViewModel.detector != null) {
                            imageCapture?.takePicture(
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageCapturedCallback() {
                                    @ExperimentalGetImage
                                    override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                        coroutineScope.launch {
                                            try {
                                                isLoading = true
                                                dialogText = "Recognizing Fish in Image..."
                                                withContext(Dispatchers.IO) {
                                                    val results: List<Classifier.Recognition> =
                                                        mainViewModel.detector.recognizeImage(
                                                            bitmap
                                                        )

                                                    Timber.i(results.toString())

                                                    val minimumConfidence: Float =
                                                        Constants.MINIMUM_CONFIDENCE_TF_OD_API

                                                    val mappedRecognitions: MutableList<Classifier.Recognition> =
                                                        LinkedList<Classifier.Recognition>()

                                                    for (result in results) {
                                                        val location = result.location
                                                        if (location != null && result.confidence >= minimumConfidence) {
                                                            mappedRecognitions.add(result)
                                                        }
                                                    }

                                                    Timber.i(mappedRecognitions.toString())

                                                    imageUri = if (mappedRecognitions.isEmpty()) {
                                                        Toast.makeText(context, "Fish not detected. Please try again.", Toast.LENGTH_LONG).show()
                                                        ""
                                                    } else {
                                                        // File saving part
                                                        dialogText = "Saving Image..."
                                                        val bmp = imageProxy.getBitmap()
                                                        val minDimension = min(bmp.width, bmp.height)
                                                        val croppedBmp = if (bmp.height > bmp.width)
                                                            Bitmap.createBitmap(
                                                                bmp,
                                                                0,
                                                                (bmp.height - minDimension) / 2,
                                                                minDimension,
                                                                minDimension
                                                            ) else
                                                            Bitmap.createBitmap(
                                                                bmp,
                                                                (bmp.width - minDimension)/2,
                                                                0,
                                                                minDimension,
                                                                minDimension
                                                            )
                                                        val randomInt = Random.nextInt()
                                                        val imageFile = File(
                                                            context.filesDir,
                                                            "fish_image_${System.currentTimeMillis()}_${randomInt}.jpg"
                                                        )
                                                        imageFile.storeBitmap(context, croppedBmp)
                                                        URLEncoder.encode(
                                                            imageFile.getUri(context).toString(),
                                                            "utf-8"
                                                        )
                                                    }
                                                    Timber.i("Done at ${DateUtils.getTimeSec(System.currentTimeMillis())}")
                                                }
                                            } catch (exception: Exception) {
                                                Timber.e(exception)
                                            } finally {
                                                if (imageUri.isNotBlank()) {
                                                    navController.navigate("enter-details/$imageUri")
                                                }
                                                isLoading = false
                                                dialogText = ""
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
                    }
                },
                enabled = !isLoading && bitmap != null,
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Filled.CameraAlt, "Camera")
            }

            if (isLoading) {
                Dialog(onDismissRequest = { /*TODO*/ }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .align(Alignment.Center)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(dialogText, style = MaterialTheme.typography.labelLarge, maxLines = 3, overflow = TextOverflow.Ellipsis)
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}