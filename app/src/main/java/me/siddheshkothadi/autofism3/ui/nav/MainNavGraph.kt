package me.siddheshkothadi.autofism3.ui.nav

import android.app.Activity
import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import me.siddheshkothadi.autofism3.MainViewModel
import me.siddheshkothadi.autofism3.ui.screen.*
import me.siddheshkothadi.autofism3.ui.viewmodel.HistoryViewModel
import timber.log.Timber

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainNavGraph(
    context: Activity,
    mainViewModel: MainViewModel,
    recreateActivity: () -> Unit
) {
    val navController = rememberAnimatedNavController()

    val startDestination by remember { mainViewModel.startDestination }

    val screens = listOf(
        Screen.Capture,
        Screen.HistoryNav,
//        Screen.Learn,
        Screen.Stats,
        Screen.Settings
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if(startDestination != Screen.SelectLanguage.route) {
                NavigationBar(
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.navigationBars.only(
                            WindowInsetsSides.Bottom
                        )
                    )
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    screens.forEach { screen ->
                        NavigationBarItem(
                            modifier = Modifier.clip(RoundedCornerShape(10.dp)),
                            icon = {
                                Icon(
                                    if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) screen.filledIcon else screen.outlinedIcon,
                                    null
                                )
                            },
                            label = { Text(stringResource(id = screen.resourceId)) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                Timber.tag("NavDebug").i(navController.backQueue.map {
                                    it.destination.route
                                }.toString())

                                if (screen.route == Screen.Capture.route) {
                                    navController.popBackStack(
                                        route = Screen.Camera.route,
                                        inclusive = false
                                    )
                                    Timber.tag("NavDebug").i(navController.backQueue.map {
                                        it.destination.route
                                    }.toString())
                                    return@NavigationBarItem
                                }

                                navController.navigate(screen.route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(Screen.Camera.route) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination when
                                    // re-selecting the same item
                                    launchSingleTop = true
                                    // Restore state when re-selecting a previously selected item
                                    restoreState = screen.route != Screen.Capture.route
                                }
                                Timber.tag("NavDebug").i(navController.backQueue.map {
                                    it.destination.route
                                }.toString())
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        AnimatedNavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            captureGraph(navController, mainViewModel, context)
            historyNavGraph(navController)
            composable(Screen.Learn.route) {
                EmptyScreen()
            }
            composable(Screen.Stats.route) {
                StatsScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController, mainViewModel, recreateActivity)
            }
            composable(Screen.SelectLanguage.route) {
                SelectLanguage(mainViewModel, recreateActivity)
            }
        }
    }
}