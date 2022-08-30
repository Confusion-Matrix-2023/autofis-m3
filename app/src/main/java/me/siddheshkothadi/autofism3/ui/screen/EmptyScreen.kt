package me.siddheshkothadi.autofism3.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import me.siddheshkothadi.autofism3.R
import me.siddheshkothadi.autofism3.ui.component.MapView
import me.siddheshkothadi.autofism3.ui.nav.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmptyScreen() {
    Column(Modifier.fillMaxSize()) {
        val list = listOf<Pair<String, String>>(
            Pair("17.4539092","78.3143516"),
            Pair("17.5682658","78.0699635"),
            Pair("18.2741989","84.0526886"),
            Pair("18.2741989","84.0526886"),
        )

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
                                stringResource(id = Screen.Stats.resourceId),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Medium
                            )
                        },
                    )
                }
            }
        ) {
            LazyColumn(Modifier.fillMaxWidth()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(it)
                            .padding(top = 24.dp, bottom = 8.dp, start = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.nearby_fishing_zones),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }
                items(list) {
                    list.forEach {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Card(Modifier.padding(12.dp)) {
                                MapView(
                                    it.first, it.second,
                                    Modifier
                                        .size(256.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}