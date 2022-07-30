package me.siddheshkothadi.autofism3.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@Composable
fun EnterDetails(
    navController: NavHostController,
    fishImageUri: String
) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(fishImageUri)
        AsyncImage(
            model = Uri.parse(fishImageUri),
            contentDescription = "Fish image",
            modifier = Modifier
                .width(250.dp)
                .height(250.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10)),
            contentScale = ContentScale.Crop
        )
    }
}