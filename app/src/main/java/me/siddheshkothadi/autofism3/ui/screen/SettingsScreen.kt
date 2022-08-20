package me.siddheshkothadi.autofism3.ui.screen

import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import me.siddheshkothadi.autofism3.MainViewModel
import me.siddheshkothadi.autofism3.ui.Language
import me.siddheshkothadi.autofism3.ui.component.LanguagePreference
import me.siddheshkothadi.autofism3.ui.nav.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    recreateActivity: () -> Unit
) {
    val context = LocalContext.current
    val sharedPref: SharedPreferences =
        remember { PreferenceManager.getDefaultSharedPreferences(context) }

    val lang = remember {
        mutableStateOf(
            Constants.availableLanguages.find { l ->
                l.locale == sharedPref.getString(Constants.LANGUAGE_KEY, Language.ENGLISH.locale)
            } ?: Language.ENGLISH
        )
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
                        Text(stringResource(id = Screen.Settings.resourceId))
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
        LazyColumn() {
            item {
                LanguagePreference(lang.value) {
                    mainViewModel.onLanguageSelected(it, recreateActivity)
                }
            }
        }
    }
}