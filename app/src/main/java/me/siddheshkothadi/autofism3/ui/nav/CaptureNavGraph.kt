package me.siddheshkothadi.autofism3.ui.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import me.siddheshkothadi.autofism3.ui.screen.CameraScreen
import me.siddheshkothadi.autofism3.ui.screen.EnterDetails
import me.siddheshkothadi.autofism3.ui.viewmodel.EnterDetailsViewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalPermissionsApi::class)
fun NavGraphBuilder.captureGraph(navController: NavHostController) {
    navigation(startDestination = Screen.Camera.route, route = Screen.Capture.route) {
        composable(Screen.Camera.route) {
            CameraScreen(navController)
        }
        composable(Screen.EnterDetails.route) {
            navController.currentBackStackEntry?.arguments?.getString("uri")?.let { fishImageUri ->
                val enterDetailsViewModel: EnterDetailsViewModel = hiltViewModel()
                EnterDetails(
                    navController,
                    enterDetailsViewModel,
                    fishImageUri
                )
            }
        }
    }
}