package me.siddheshkothadi.autofism3.ui.screen

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.siddheshkothadi.autofism3.model.PendingUploadFish
import me.siddheshkothadi.autofism3.ui.nav.Screen
import me.siddheshkothadi.autofism3.ui.viewmodel.ViewDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewDetails(navController: NavHostController, fishImageUri: String, viewDetailsViewModel: ViewDetailsViewModel) {
    var fish: PendingUploadFish? = remember { null }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = fishImageUri) {
        coroutineScope.launch(Dispatchers.IO) {
            fish = viewDetailsViewModel.getFishData(fishImageUri)
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
                            stringResource(id = Screen.ViewDetails.resourceId),
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
            if(fish == null) {
                CircularProgressIndicator()
            } else {
                AsyncImage(
                    model = Uri.parse(fishImageUri),
                    contentDescription = "Fish image",
                    modifier = Modifier
                        .height(256.dp)
                        .width(256.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }
    }
}