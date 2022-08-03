package me.siddheshkothadi.autofism3.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.siddheshkothadi.autofism3.ui.component.AppBar
import me.siddheshkothadi.autofism3.ui.nav.Screen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun History(
) {
    Scaffold(
        topBar = {
            AppBar(stringResource(id = Screen.History.resourceId))
        },
    ) {
        LazyColumn(
            modifier = Modifier.padding(12.dp),
            contentPadding = PaddingValues(bottom = 180.dp)
        ) {
            item {
                Text("Pending Uploads", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                LazyRow() {
                    items(10) {
                        Row {
                            ElevatedCard() {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    AsyncImage(
                                        model = "https://i.insider.com/57a4db38dd089551028b465b?width=1136&format=jpeg",
                                        contentDescription = "Fish Image",
                                        modifier = Modifier
                                            .width(128.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("Gold Fish", style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("16 July 2022", style = MaterialTheme.typography.bodySmall)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("9:38 AM", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Upload History", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(10) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    ElevatedCard(
                        Modifier
                            .fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = "https://static.wixstatic.com/media/42a449_03b0ddbdee3f41c4a6bc347620c039bd~mv2.png/v1/fill/w_220,h_220,al_c,q_85,usm_0.66_1.00_0.01,enc_auto/logo-main-know-your-fish_square.png",
                                contentDescription = "Fish Image",
                                modifier = Modifier
                                    .width(82.dp)
                                    .height(82.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Column {
                                Text("XYZ Fish", style = MaterialTheme.typography.labelLarge)
                                Text("16 July 2022", style = MaterialTheme.typography.labelSmall)
                                Text("9:38 AM", style = MaterialTheme.typography.labelSmall)
                            }
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(Icons.Filled.PinDrop, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }
}