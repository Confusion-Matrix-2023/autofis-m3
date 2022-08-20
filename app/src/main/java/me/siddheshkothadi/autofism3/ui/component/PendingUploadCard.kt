package me.siddheshkothadi.autofism3.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.work.WorkInfo
import androidx.work.WorkManager
import coil.compose.AsyncImage
import me.siddheshkothadi.autofism3.model.PendingUploadFish
import me.siddheshkothadi.autofism3.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingUploadCard(
    fish: PendingUploadFish
) {
    val context = LocalContext.current
    val workManager = WorkManager.getInstance(context)

    val workState = workManager.getWorkInfoByIdLiveData(fish.workId).observeAsState()

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
                text = DateUtils.getDate(context, fish.timestamp),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = DateUtils.getTime(context, fish.timestamp),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )

            if(workState.value?.state == WorkInfo.State.RUNNING) {
                LinearProgressIndicator(Modifier.width(128.dp).padding(horizontal = 4.dp, vertical = 2.dp))
            }
        }
    }
}