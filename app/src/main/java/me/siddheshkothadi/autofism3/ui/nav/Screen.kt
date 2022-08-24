package me.siddheshkothadi.autofism3.ui.nav

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import me.siddheshkothadi.autofism3.R

sealed class Screen(
    val route: String,
    @StringRes val resourceId: Int,
    val outlinedIcon: ImageVector,
    val filledIcon: ImageVector
) {
    object Capture : Screen("capture", R.string.capture, Icons.Outlined.Camera, Icons.Filled.Camera)
    object Camera : Screen("camera", R.string.capture, Icons.Outlined.Camera, Icons.Filled.Camera)
    object EnterDetails :
        Screen("enter-details/{uri}", R.string.enter_details, Icons.Outlined.Camera, Icons.Filled.Camera)

    object HistoryNav: Screen("history-nav", R.string.history, Icons.Outlined.History, Icons.Filled.History)
    object History :
        Screen("history", R.string.history, Icons.Outlined.History, Icons.Filled.History)
    object ViewDetails :
        Screen("view-details/{uri}", R.string.view_details, Icons.Outlined.History, Icons.Filled.History)

    object Learn : Screen(
        "learn",
        R.string.learn,
        Icons.Outlined.CastForEducation,
        Icons.Filled.CastForEducation
    )

    object Stats : Screen("stats", R.string.stats, Icons.Outlined.AreaChart, Icons.Filled.AreaChart)
    object Settings :
        Screen("settings", R.string.settings, Icons.Outlined.Settings, Icons.Filled.Settings)

    object SelectLanguage :
            Screen("select-language", R.string.select_language, Icons.Outlined.Settings, Icons.Filled.Settings)
}