package me.siddheshkothadi.autofism3.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppBar() {
    Surface(
        tonalElevation = 3.dp, modifier = Modifier.windowInsetsPadding(
            WindowInsets.statusBars.only(
                WindowInsetsSides.Top
            )
        )
    ) {
        CenterAlignedTopAppBar(
            title = { Text("History") }
        )
    }
}