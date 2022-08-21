package me.siddheshkothadi.autofism3.ui.screen

import android.Manifest
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import me.siddheshkothadi.autofism3.R
import me.siddheshkothadi.autofism3.ui.component.MapView
import me.siddheshkothadi.autofism3.ui.nav.Screen
import me.siddheshkothadi.autofism3.ui.viewmodel.EnterDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun EnterDetails(
    navController: NavHostController,
    enterDetailsViewModel: EnterDetailsViewModel,
    fishImageUri: String
) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    )

    LaunchedEffect(permissionsState) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

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
            AsyncImage(
                model = Uri.parse(fishImageUri),
                contentDescription = "Fish image",
                modifier = Modifier
                    .height(256.dp)
                    .width(256.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
            )

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

            if(latitude.isNotBlank() && longitude.isNotBlank()) {
                MapView(
                    latitude, longitude,
                    Modifier
                        .size(256.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                if(isLoading) {
                    CircularProgressIndicator(Modifier.size(20.dp))
                }
                else {
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