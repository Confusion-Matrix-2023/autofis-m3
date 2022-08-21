package me.siddheshkothadi.autofism3

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.hilt.android.AndroidEntryPoint
import me.siddheshkothadi.autofism3.datastore.LocalDataStore
import me.siddheshkothadi.autofism3.ui.nav.MainNavGraph
import me.siddheshkothadi.autofism3.ui.theme.AutoFISM3Theme
import me.siddheshkothadi.autofism3.utils.setAppLocale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var localDataStore: LocalDataStore

    @OptIn(
        ExperimentalPermissionsApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                mainViewModel.isLoading.value
            }
        }
        super.onCreate(savedInstanceState)
        setContent {
            AutoFISM3Theme {
                MainNavGraph(this, mainViewModel = mainViewModel) {
                    recreate()
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(newBase)
        if (sharedPref != null) {
            val languageLocale = sharedPref.getString(Constants.LANGUAGE_KEY, "")
            if (!languageLocale.isNullOrBlank()) {
                // Language set, use that language
                super.attachBaseContext(ContextWrapper(newBase.setAppLocale(languageLocale)))
                return
            }
        }

        // Shared preferences null then set default language
        // Language not set yet then English to default
        super.attachBaseContext(ContextWrapper(newBase.setAppLocale(Constants.DEFAULT_LOCALE)))
    }
}
