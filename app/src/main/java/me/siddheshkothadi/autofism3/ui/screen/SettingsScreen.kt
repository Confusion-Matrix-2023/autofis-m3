package me.siddheshkothadi.autofism3.ui.screen

import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import me.siddheshkothadi.autofism3.Constants
import me.siddheshkothadi.autofism3.R
import me.siddheshkothadi.autofism3.ui.Language
import me.siddheshkothadi.autofism3.ui.nav.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val sharedPref: SharedPreferences = remember {PreferenceManager.getDefaultSharedPreferences(context) }

    val lang = remember { mutableStateOf(Language.ENGLISH) }

    val once = remember { mutableStateOf(true) }

    LaunchedEffect(once) {
        val currentLanguage = sharedPref.getString(Constants.LANGUAGE_KEY, Language.ENGLISH.locale)
        lang.value = if (currentLanguage.isNullOrBlank()) {
            Language.ENGLISH
        }
        else Constants.availableLanguages.find { l -> l.locale == currentLanguage } ?: Language.ENGLISH
    }

    Scaffold(
        topBar = {
            Surface(
                tonalElevation = 3.dp,
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.statusBars.only(
                        WindowInsetsSides.Top
                    )
                )
            ) {
                LargeTopAppBar(
                    title = {
                        Text(stringResource(id = Screen.Settings.resourceId),)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back Arrow"
                            )
                        }
                    }
                )
            }
        }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(it)
                .padding(20.dp)
        ) {
            Text(stringResource(R.string.language), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(24.dp))

            Text(stringResource(R.string.chosen_language), style = MaterialTheme.typography.titleMedium)
            Text(lang.value.langName, style = MaterialTheme.typography.titleLarge)



        }
    }
}