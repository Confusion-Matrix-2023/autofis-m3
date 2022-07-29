package me.siddheshkothadi.autofism3

import android.Manifest
import android.hardware.camera2.CameraManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.Camera
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import me.siddheshkothadi.autofism3.ui.screens.Login
import me.siddheshkothadi.autofism3.ui.screens.MainLayout
import me.siddheshkothadi.autofism3.ui.theme.AutoFISM3Theme

class MainActivity : ComponentActivity() {
    private lateinit var camera: Camera

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class,
        ExperimentalPermissionsApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberAnimatedNavController()

            AutoFISM3Theme {
                val permissionsState = rememberMultiplePermissionsState(
                    permissions = listOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )

                LaunchedEffect(permissionsState) {
                    if (!permissionsState.allPermissionsGranted) {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AnimatedNavHost(
                        navController,
                        startDestination = "mainLayout"
                    ) {
                        composable(
                            "login"
                        ) {
                            Login(navController)
                        }

                        composable(
                            "mainLayout"
                        ) {
                            MainLayout()
                        }
                    }
                }
            }
        }
    }
}
