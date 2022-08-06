package me.siddheshkothadi.autofism3.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.siddheshkothadi.autofism3.model.PendingUploadFish
import me.siddheshkothadi.autofism3.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingUploadCard(
    fish: PendingUploadFish
) {
    ElevatedCard(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 4.dp, end = 4.dp, bottom = 10.dp)
        ) {
            AsyncImage(
                model = fish.imageUri,
                contentDescription = "Fish Image",
                modifier = Modifier
                    .size(128.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = DateUtils.getDate(fish.timestamp),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = DateUtils.getTime(fish.timestamp),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
//            Text(
//                text = "${fish.quantity} kg",
//                style = MaterialTheme.typography.bodySmall,
//                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
//            )
        }
    }
}