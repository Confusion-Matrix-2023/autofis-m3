package me.siddheshkothadi.autofism3.ui.screen

import android.Manifest
import android.app.Activity
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.text.TextUtils
import android.util.TypedValue
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
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
    val isConnectedToNetwork by remember { enterDetailsViewModel.isConnectedToNetwork }

    var quantityError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    val selectedBox = remember { enterDetailsViewModel.selectedBox }
    var expanded by remember { mutableStateOf(false) }

    val paintConfig = remember {
        Paint().apply {
            color = android.graphics.Color.BLUE
            strokeWidth = 7.0f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            strokeMiter = 100f
        }
    }

    val redPaintConfig = remember {
        Paint().apply {
            color = android.graphics.Color.RED
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
//                        .height(256.dp)
//                        .width(256.dp)
                        .aspectRatio(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                )
                if(bitmapInfo.value.bitmapHeight != 0 && bitmapInfo.value.bitmapWidth != 0) {
                    Canvas(
                        modifier = Modifier
//                            .height(256.dp)
//                            .width(256.dp)
                            .aspectRatio(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        val scaleY = size.height * 1f / bitmapInfo.value.bitmapWidth
                        val scaleX = size.width * 1f / bitmapInfo.value.bitmapHeight
                        boundingBoxes.value.forEachIndexed { index, it ->
                            val cornerSize: Float =
                                min(it.width(), it.height()) / 8.0f
                            val width = exteriorPaint.measureText("0")
                            val textSize = exteriorPaint.textSize
                            val paint = Paint(paintConfig)
                            val redPaint = Paint(redPaintConfig)
                            paint.style = Paint.Style.FILL
                            paint.alpha = 160
                            redPaint.style = Paint.Style.FILL
                            redPaint.alpha = 160
                            val posX = it.left + cornerSize
                            val posY = it.top

                            val labelString = index.toString()

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
                                    if(selectedBox.value == index) redPaintConfig else paintConfig
                                )
                                drawRect(
                                    posX * scaleX,
                                    (posY + textSize.toInt()) * scaleY,
                                    (posX + width.toInt() * scaleX) * scaleX,
                                    posY * scaleY,
                                    if(selectedBox.value == index) redPaint else paint
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


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                OutlinedTextField(
                    value = selectedBox.value.toString(),
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    label = {
                        Text("Selected Box", color = Color.Gray)
                    },
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(Icons.Filled.ArrowDropDown, null)
                        }
                    }
                )
                DropdownMenu(
                    expanded = expanded,
                    modifier = Modifier.fillMaxWidth(),
                    offset = DpOffset(x = (0).dp, y = (-10).dp),
                    onDismissRequest = { expanded = false }
                ) {
                    boundingBoxes.value.forEachIndexed { index, _ ->
                        DropdownMenuItem(
                            text = { Text(index.toString()) },
                            onClick = {
                                selectedBox.value = index
                                expanded = false
                            }
                        )
                    }
                }
            }

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
                if(!isConnectedToNetwork) {
                    Text(stringResource(R.string.map_view_may_not_render_properly), color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(12.dp))
                }
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
                    Text(stringResource(id = R.string.location_not_found), color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                }
            }

            Button(
                modifier = Modifier
                    .width(300.dp)
                    .padding(vertical = 24.dp),
                onClick = {
                    try {
                        val quantityToInt = quantity.toInt()
                        if (quantityToInt >= 0) {
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
                        } else {
                            throw Exception()
                        }
                    } catch (e: Exception) {
                        quantityError = true

                        Toast.makeText(
                            context,
                            "Please enter a valid quantity",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }) {
                Text(stringResource(R.string.submit))
            }
        }
    }
}