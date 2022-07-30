package me.siddheshkothadi.autofism3

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import me.siddheshkothadi.autofism3.ui.nav.Screen
import me.siddheshkothadi.autofism3.ui.nav.captureGraph
import me.siddheshkothadi.autofism3.ui.screens.CameraScreen
import me.siddheshkothadi.autofism3.ui.screens.EmptyScreen
import me.siddheshkothadi.autofism3.ui.screens.History
import me.siddheshkothadi.autofism3.ui.theme.AutoFISM3Theme

class MainActivity : ComponentActivity() {
    @OptIn(
        ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class,
        ExperimentalPermissionsApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            AutoFISM3Theme {
                val navController = rememberAnimatedNavController()

                val screens = listOf(
                    Screen.Capture,
                    Screen.History,
                    Screen.Learn,
                    Screen.Stats,
                    Screen.Settings
                )

                val permissionsState = rememberMultiplePermissionsState(
                    permissions = listOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                    )
                )

                LaunchedEffect(permissionsState) {
                    if (!permissionsState.allPermissionsGranted) {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
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
                                        navController.navigate(screen.route) {
                                            // Pop up to the start destination of the graph to
                                            // avoid building up a large stack of destinations
                                            // on the back stack as users select items
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            // Avoid multiple copies of the same destination when
                                            // re-selecting the same item
                                            launchSingleTop = true
                                            // Restore state when re-selecting a previously selected item
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    AnimatedNavHost(
                        navController = navController,
                        startDestination = Screen.Capture.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        captureGraph(navController)
                        composable(Screen.History.route) {
                            History()
                        }
                        composable(Screen.Learn.route) {
                            EmptyScreen()
                        }
                        composable(Screen.Stats.route) {
                            EmptyScreen()
                        }
                        composable(Screen.Settings.route) {
                            EmptyScreen()
                        }
                    }
                }
            }
        }
    }
}
