package me.siddheshkothadi.autofism3.ui.component

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import me.siddheshkothadi.autofism3.R
import me.siddheshkothadi.autofism3.model.UploadHistoryFish
import me.siddheshkothadi.autofism3.utils.DateUtils

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
            .clickable { },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = fish.image_url,
                contentDescription = "Fish Image",
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(fish.name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 8.dp), fontSize = 19.sp)
                    Text("${DateUtils.getDateWithoutYear(context, fish.timestamp)}, ${DateUtils.getTime(context, fish.timestamp)}", style = MaterialTheme.typography.bodySmall)
//                    Text(DateUtils.getTime(context, fish.timestamp), style = MaterialTheme.typography.bodySmall)

                    Row(modifier = Modifier.padding(top = 12.dp, bottom=4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Thermostat, "Thermostat", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        fish.temp?.let { Text(stringResource(id = R.string.degree_celsius, it.toFloat().toInt().toString()), style = MaterialTheme.typography.bodySmall) }
                        Spacer(Modifier.width(12.dp))
                        Icon(Icons.Outlined.WaterDrop, "WaterDrop", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        fish.humidity?.let { Text(stringResource(id = R.string.humidity_percent, it), style = MaterialTheme.typography.bodySmall) }
                    }
                    Row(modifier = Modifier.padding(bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Air, "Air", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        fish.speed?.let { Text(stringResource(id = R.string.km_per_hr, (it.toFloat()*3.6).toInt().toString()), style = MaterialTheme.typography.bodySmall) }
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Outlined.NorthEast, "Angle", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        fish.deg?.let { Text(stringResource(id = R.string.wind_deg, it), style = MaterialTheme.typography.bodySmall) }
                    }
                }
                if(fish.latitude.isNotBlank() && fish.longitude.isNotBlank()) {
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