package me.siddheshkothadi.autofism3.utils

import android.content.Context
import java.util.*

fun Context.updateAppLocale(language: String) {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    config.setLayoutDirection(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
}

fun Context.setAppLocale(language: String): Context {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    config.setLayoutDirection(locale)
    return createConfigurationContext(config)
}