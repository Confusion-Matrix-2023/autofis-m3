package me.siddheshkothadi.autofism3

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.siddheshkothadi.autofism3.detection.tflite.DetectorFactory
import me.siddheshkothadi.autofism3.detection.tflite.YoloV5Classifier
import me.siddheshkothadi.autofism3.ui.Language
import me.siddheshkothadi.autofism3.ui.nav.Screen
import me.siddheshkothadi.autofism3.utils.setAppLocale
import me.siddheshkothadi.autofism3.utils.updateAppLocale
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    app: FishApplication
) : ViewModel() {

    private val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)
    val isLanguageSelected: MutableState<Boolean> = mutableStateOf(false)
    val detector: MutableState<YoloV5Classifier?> = mutableStateOf(null)
    val isLoading = mutableStateOf(true)

    val availableLanguages = listOf(
        Language.ENGLISH,
        Language.HINDI,
    )

    var selectedLanguage = mutableStateOf(Language.ENGLISH)

    init {
        Timber.i("Init Block")
        viewModelScope.launch(Dispatchers.IO) {
            viewModelScope.launch(Dispatchers.Main) {
                isLoading.value = true
            }
            viewModelScope.launch(Dispatchers.Main) {
                checkLanguage()
            }
            detector.value = DetectorFactory.getDetector(
                app.assets,
                Constants.MODEL_FILE_PATH
            )

            viewModelScope.launch(Dispatchers.Main) {
                isLoading.value = false
            }
        }
    }

    private fun checkLanguage() {
        val currentLanguage = sharedPref.getString(Constants.LANGUAGE_KEY, "")
        if (!currentLanguage.isNullOrBlank()) {
            // Language set
            isLanguageSelected.value = true
        }
    }

    fun onRadioButtonSelected(context: Context, language: Language, recreateActivity: () -> Unit) {
        selectedLanguage.value = language
        context.updateAppLocale(language.locale)
        recreateActivity()
    }

    fun onLanguageChosen(recreateActivity: () -> Unit) {
        setLanguage(selectedLanguage.value.locale)
        recreateActivity()
        checkLanguage()
    }

    private fun setLanguage(language: String) {
        with(sharedPref.edit()) {
            putString(Constants.LANGUAGE_KEY, language)
            apply()
        }
    }
}