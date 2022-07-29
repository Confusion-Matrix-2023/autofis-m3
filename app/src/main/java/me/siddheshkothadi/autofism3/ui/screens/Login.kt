package me.siddheshkothadi.autofism3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import me.siddheshkothadi.autofism3.R

@Composable
fun Login(navHostController: NavHostController) {
    val lottieCompositionSpec = LottieCompositionSpec.RawRes(R.raw.fishing)
    val composition by rememberLottieComposition(lottieCompositionSpec)
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
    )
    Surface(tonalElevation = 2.dp) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "AutoFIS", style = MaterialTheme.typography.headlineLarge)
            LottieAnimation(
                composition,
                progress,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Column(Modifier.padding(24.dp)) {
                Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Login")
                }
                Spacer(Modifier.height(13.dp))
                OutlinedButton(onClick = {
                    navHostController.navigate("mainLayout") {
                        popUpTo("login") { inclusive = true }
                    }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Continue without Login")
                }
            }

        }
    }
}