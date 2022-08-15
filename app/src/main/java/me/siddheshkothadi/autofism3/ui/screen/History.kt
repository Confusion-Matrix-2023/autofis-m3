package me.siddheshkothadi.autofism3.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.work.WorkInfo
import androidx.work.WorkManager
import me.siddheshkothadi.autofism3.ui.component.AppBar
import me.siddheshkothadi.autofism3.ui.component.PendingUploadCard
import me.siddheshkothadi.autofism3.ui.component.UploadHistoryCard
import me.siddheshkothadi.autofism3.ui.nav.Screen
import me.siddheshkothadi.autofism3.ui.viewmodel.HistoryViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun History(
    historyViewModel: HistoryViewModel
) {
    val tag = "UPLOAD_REQUEST"
    val context = LocalContext.current

    val workManager = remember { WorkManager.getInstance(context) }
    val workState by workManager.getWorkInfosByTagLiveData(tag).observeAsState()

    val pendingUploads by historyViewModel.pendingUploads.collectAsState(initial = listOf())
    val uploadHistory by historyViewModel.uploadHistory.collectAsState(initial = listOf())
    val isFetching by remember { historyViewModel.isFetching }

    Scaffold(
        topBar = {
            AppBar(stringResource(id = Screen.History.resourceId))
        },
    ) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 12.dp),
            contentPadding = PaddingValues(bottom = 56.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (pendingUploads.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pending Uploads",
                            style = MaterialTheme.typography.titleLarge,
                        )
                        if (workState?.any {
                                it.state == WorkInfo.State.RUNNING
                            } == true
                        ) {
                            CircularProgressIndicator(Modifier.size(20.dp))
                        }
                    }
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(pendingUploads) { fish ->
                        PendingUploadCard(fish)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Upload History",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    if (isFetching) {
                        CircularProgressIndicator(Modifier.size(20.dp))
                    } else {
                        TextButton(onClick = { historyViewModel.fetchUploadHistory() }) {
                            Text("Fetch")
                        }
                    }
                }
            }

            items(uploadHistory) { fish ->
                UploadHistoryCard(fish)
            }
        }
    }
}