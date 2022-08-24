package me.siddheshkothadi.autofism3.ui.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import me.siddheshkothadi.autofism3.ui.screen.History
import me.siddheshkothadi.autofism3.ui.viewmodel.HistoryViewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalPermissionsApi::class)
fun NavGraphBuilder.historyNavGraph(navController: NavHostController) {
    navigation(startDestination = Screen.History.route, route = Screen.HistoryNav.route) {
        composable(Screen.History.route) {
            val historyViewModel: HistoryViewModel = hiltViewModel()
            History(historyViewModel)
        }
        composable(Screen.ViewDetails.route) {
            navController.currentBackStackEntry?.arguments?.getString("uri")?.let { fishImageUri ->

            }
        }
    }
}