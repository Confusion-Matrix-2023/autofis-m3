package me.siddheshkothadi.autofism3.ui.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import me.siddheshkothadi.autofism3.ui.screens.CameraScreen
import me.siddheshkothadi.autofism3.ui.screens.EnterDetails

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.captureGraph(navController: NavHostController) {
    navigation(startDestination = Screen.Camera.route, route = Screen.Capture.route) {
        composable(Screen.Camera.route) {
            CameraScreen(navController)
        }
        composable(Screen.EnterDetails.route) {
            EnterDetails()
        }
    }
}