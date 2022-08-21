package me.siddheshkothadi.autofism3.ui.screen

import android.Manifest
import android.app.Activity
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.text.TextUtils
import android.util.TypedValue
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import me.siddheshkothadi.autofism3.R
import me.siddheshkothadi.autofism3.datastore.BitmapInfo
import me.siddheshkothadi.autofism3.ui.component.MapView
import me.siddheshkothadi.autofism3.ui.nav.Screen
import me.siddheshkothadi.autofism3.ui.viewmodel.EnterDetailsViewModel
import java.lang.Float.min

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun EnterDetails(
    navController: NavHostController,
    enterDetailsViewModel: EnterDetailsViewModel,
    fishImageUri: String,
    activityContext: Activity
) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    )

    LaunchedEffect(permissionsState) {
        enterDetailsViewModel.checkLocationAccess(activityContext)
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    val bitmapInfo = enterDetailsViewModel.bitmapInfo.collectAsState(initial = BitmapInfo.getDefaultInstance())
    val boundingBoxes = enterDetailsViewModel.boundingBoxes.collectAsState(initial = listOf())

    val isLoading by remember { enterDetailsViewModel.isLoading }
    val quantity by remember { enterDetailsViewModel.quantity }
    val latitude by remember { enterDetailsViewModel.latitude }
    val longitude by remember { enterDetailsViewModel.longitude }
    val date by enterDetailsViewModel.dateString.collectAsState(initial = "Loading...")
    val time by enterDetailsViewModel.timeString.collectAsState(initial = "Loading...")

    var quantityError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    val paintConfig = remember {
        Paint().apply {
            color = android.graphics.Color.BLUE
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
            color = android.graphics.Color.WHITE
            style = Paint.Style.FILL
            isAntiAlias = false
            alpha = 255
        }
    }

    val exteriorPaint = remember {
        Paint().apply {
            textSize = textSizePx
            color = android.graphics.Color.BLUE
            style = Paint.Style.FILL
            isAntiAlias = false
            alpha = 255
        }
    }

    Scaffold(
        topBar = {
            Surface(
                tonalElevation = 3.dp,
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.statusBars.only(
                        WindowInsetsSides.Top
                    )
                )
            ) {
                SmallTopAppBar(
                    title = {
                        Text(
                            stringResource(id = Screen.EnterDetails.resourceId),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back Arrow"
                            )
                        }
                    }
                )
            }
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .height(256.dp)
                    .width(256.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                AsyncImage(
                    model = Uri.parse(fishImageUri),
                    contentDescription = "Fish image",
                    modifier = Modifier
                        .height(256.dp)
                        .width(256.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                )
                if(bitmapInfo.value.bitmapHeight != 0 && bitmapInfo.value.bitmapWidth != 0) {
                    Canvas(
                        modifier = Modifier
                            .height(256.dp)
                            .width(256.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        val scaleY = size.height * 1f / bitmapInfo.value.bitmapWidth
                        val scaleX = size.width * 1f / bitmapInfo.value.bitmapHeight
                        boundingBoxes.value.forEach {
                            val cornerSize: Float =
                                min(it.width(), it.height()) / 8.0f
                            val width = exteriorPaint.measureText(context.getString(R.string.fish))
                            val textSize = exteriorPaint.textSize
                            val paint = Paint(paintConfig)
                            paint.style = Paint.Style.FILL
                            paint.alpha = 160
                            val posX = it.left + cornerSize
                            val posY = it.top

                            val labelString = context.getString(R.string.fish)

                            drawContext.canvas.nativeCanvas.apply {
                                drawRoundRect(
                                    RectF(
                                        it.left * scaleX,
                                        it.top * scaleY,
                                        it.right * scaleX,
                                        it.bottom * scaleY
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
            }

            Spacer(Modifier.height(18.dp))

            Text(date)
            Text(time)

            OutlinedTextField(
                value = quantity,
                onValueChange = {
                    enterDetailsViewModel.setQuantity(it)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                label = {
                    Text(stringResource(R.string.quantity_label), color = Color.Gray)
                },
                isError = quantityError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            )

            if (latitude.isNotBlank() && longitude.isNotBlank()) {
                MapView(
                    latitude, longitude,
                    Modifier
                        .size(256.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.size(20.dp))
                } else {
                    Text(stringResource(id = R.string.location_not_found))
                }
            }

            Button(
                modifier = Modifier
                    .width(300.dp)
                    .padding(vertical = 24.dp),
                onClick = {
                    if (quantity == "0" || quantity == "") {
                        quantityError = true
                        Toast.makeText(
                            context,
                            context.getString(R.string.please_enter_a_valid_quantity),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        coroutineScope.launch {
                            enterDetailsViewModel.enqueueDataUploadRequest(fishImageUri)
                            navController.navigate(Screen.History.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(Screen.Camera.route) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // re-selecting the same item
                                launchSingleTop = true
                                // Restore state when re-selecting a previously selected item
                                restoreState = true
                            }
                        }
                    }
                }) {
                Text(stringResource(R.string.submit))
            }
        }
    }
}