package me.siddheshkothadi.autofism3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout() {
    var selectedTab by remember { mutableStateOf(2) }
    val tabs = listOf("History", "Learn", "Capture", "Stats", "Settings")

    val filledIcons = listOf(
        Icons.Filled.History,
        Icons.Filled.CastForEducation,
        Icons.Filled.Camera,
        Icons.Filled.AreaChart,
        Icons.Filled.Settings,
    )

    val outlinedIcons = listOf(
        Icons.Outlined.History,
        Icons.Outlined.CastForEducation,
        Icons.Outlined.Camera,
        Icons.Outlined.AreaChart,
        Icons.Outlined.Settings,
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.navigationBars.only(
                        WindowInsetsSides.Bottom
                    )
                )
            ) {
                tabs.forEachIndexed { index, item ->
                    NavigationBarItem(
                        modifier = Modifier.clip(RoundedCornerShape(10.dp)),
                        icon = {
                            if (selectedTab == index) {
                                Icon(
                                    filledIcons[index],
                                    contentDescription = null
                                )
                            } else {
                                Icon(
                                    outlinedIcons[index],
                                    contentDescription = null
                                )
                            }
                        },
                        label = { Text(item) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> History(paddingValues)
            2 -> CameraScreen(paddingValues)
            else -> EmptyScreen()
        }
    }
}