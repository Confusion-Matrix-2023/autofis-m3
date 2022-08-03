package me.siddheshkothadi.autofism3.ui.screen

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import me.siddheshkothadi.autofism3.ui.nav.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterDetails(
    navController: NavHostController,
    fishImageUri: String
) {
    var quantity by remember { mutableStateOf("") }
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
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = Uri.parse(fishImageUri),
                contentDescription = "Fish image",
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(250.dp)
                    .height(250.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10)),
                contentScale = ContentScale.Crop
            )

            Text("XYZ Fish", style = MaterialTheme.typography.titleLarge)
            Text("Today, 22 Aug 2022")
            Text("09:32 AM")

            OutlinedTextField(
                value = quantity,
                onValueChange = {
                    quantity = it
                },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth().padding(20.dp)
            )

            Button(onClick = {}) {
                Text("Upload")
            }
        }
    }
}