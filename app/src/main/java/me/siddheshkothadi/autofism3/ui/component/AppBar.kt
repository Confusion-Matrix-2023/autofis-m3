package me.siddheshkothadi.autofism3.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppBar(
    title: String,
    containsBackButton: Boolean = false
) {
    Surface(
        tonalElevation = 3.dp,
        modifier = Modifier.windowInsetsPadding(
            WindowInsets.statusBars.only(
                WindowInsetsSides.Top
            )
        )
    ) {
        CenterAlignedTopAppBar(
            title = { Text(title) }
        )
    }
}