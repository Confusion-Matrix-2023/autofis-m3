package me.siddheshkothadi.autofism3.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.siddheshkothadi.autofism3.model.UploadHistoryFish
import me.siddheshkothadi.autofism3.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadHistoryCard(
    fish: UploadHistoryFish
) {
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
            AsyncImage(
                model = fish.image_url,
                contentDescription = "Fish Image",
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(fish.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 4.dp))
                    Text(DateUtils.getDate(fish.timestamp), style = MaterialTheme.typography.bodySmall)
                    Text(DateUtils.getTime(fish.timestamp), style = MaterialTheme.typography.bodySmall)
//                    Text("${fish.quantity} kg", style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.PinDrop, contentDescription = null)
                }
            }
        }
    }
}