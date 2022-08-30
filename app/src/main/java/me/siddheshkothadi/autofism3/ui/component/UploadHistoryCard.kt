package me.siddheshkothadi.autofism3.ui.component

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.siddheshkothadi.autofism3.model.UploadHistoryFish
import me.siddheshkothadi.autofism3.utils.DateUtils
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadHistoryCard(
    fish: UploadHistoryFish
) {
    val context = LocalContext.current
    ElevatedCard(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {  },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            fish.image_url?.let {
                Timber.i(it)
                AsyncImage(
                    model = it,
                    contentDescription = "Fish Image",
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    fish.prediction?.let { Text(it, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 4.dp)) }
                    fish.submission_timestamp?.let {
                        Text(it.split("T")[0], style = MaterialTheme.typography.bodySmall)
                        Text(it.split("T")[1], style = MaterialTheme.typography.bodySmall)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        fish.temperature?.let {Icon(Icons.Filled.Thermostat, null) }
                        fish.temperature?.let { Text("$it°C", style = MaterialTheme.typography.bodySmall) }
                        Spacer(Modifier.width(12.dp))
                        fish.humidity?.let {Icon(Icons.Filled.WaterDrop, null)}
                        fish.humidity?.let { Text("$it%", style = MaterialTheme.typography.bodySmall) }
                        Spacer(Modifier.width(12.dp))
                    }
                }
                if(!fish.latitude.isNullOrBlank() && !fish.longitude.isNullOrBlank()) {
                    IconButton(onClick = {
                        val gmmIntentUri = Uri.parse("geo:${fish.latitude},${fish.longitude}?q=${fish.latitude},${fish.longitude}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                    }) {
                        Icon(Icons.Filled.PinDrop, contentDescription = null)
                    }
                }
            }
        }
    }
}