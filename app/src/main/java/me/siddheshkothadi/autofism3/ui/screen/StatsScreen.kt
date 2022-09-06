package me.siddheshkothadi.autofism3.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.LatLng
import me.siddheshkothadi.autofism3.R
import me.siddheshkothadi.autofism3.ui.component.MapView
import me.siddheshkothadi.autofism3.ui.nav.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen() {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            Surface(
                tonalElevation = 3.dp,
                modifier = Modifier.systemBarsPadding()
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(stringResource(id = Screen.Stats.resourceId))
                    },
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 12.dp),
            contentPadding = PaddingValues(bottom = 56.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Top catch",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = "https://cdn-acgla.nitrocdn.com/bvIhcJyiWKFqlMsfAAXRLitDZjWdRlLX/assets/static/optimized/rev-5131b73/wp-content/uploads/2021/01/shutterstock_117425023-1-scaled.jpg",
                        contentDescription = null,
                        modifier = Modifier
                            .width(200.dp)
                            .clip(RoundedCornerShape(24.dp))
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "4",
                            style = MaterialTheme.typography.headlineLarge,
                            fontSize = 64.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text("catches", style = MaterialTheme.typography.headlineSmall)
                        Text("(Tuna)", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Catches by month",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp, bottom = 8.dp, start = 10.dp, end = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {

                    val values: Map<String, Dp> = mutableMapOf(
                        "Jan" to 10.dp,
                        "Feb" to 10.dp,
                        "Mar" to 10.dp,
                        "Apr" to 10.dp,
                        "May" to 10.dp,
                        "Jun" to 10.dp,
                        "Jul" to 10.dp,
                        "Aug" to 228.dp,
                        "Sep" to 148.dp,
                        "Oct" to 10.dp,
                        "Nov" to 10.dp,
                        "Dec" to 10.dp
                    )

                    values.forEach {
                        Column(
                            Modifier.width(26.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                Modifier
                                    .height(it.value)
                                    .width(14.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(it.key, fontSize = 10.sp)
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.nearby_fishing_zones),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }

            val mapItems = mutableMapOf(
                "Green Thumb" to LatLng(18.4268755, 73.7607394),
                "Fishrieskonkan" to LatLng(18.5317816, 73.8966072),
            )

            item {
                mapItems.forEach {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                text = it.key,
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                MapView(
                                    it.value.latitude.toString(),
                                    it.value.longitude.toString(),
                                    Modifier
                                        .size(128.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 24.dp),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                ) {
                                    Button(
                                        onClick = {
                                            val gmmIntentUri =
                                                Uri.parse("google.navigation:?q=${it.value.latitude},${it.value.longitude}")
                                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                            mapIntent.setPackage("com.google.android.apps.maps")
                                            context.startActivity(mapIntent)
                                        }, shape = CircleShape,
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier
                                            .size(58.dp)
                                    ) {
                                        Icon(Icons.Filled.Directions, "Directions")
                                    }

                                    Button(
                                        onClick = {
                                            val gmmIntentUri =
                                                Uri.parse("geo:${it.value.latitude},${it.value.longitude}?q=${it.value.latitude},${it.value.longitude}")
                                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                            mapIntent.setPackage("com.google.android.apps.maps")
                                            context.startActivity(mapIntent)
                                        }, shape = CircleShape,
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier
                                            .size(58.dp)
                                    ) {
                                        Icon(Icons.Filled.PinDrop, "Maps")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}