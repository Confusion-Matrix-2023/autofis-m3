package me.siddheshkothadi.autofism3.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import me.siddheshkothadi.autofism3.Constants
import me.siddheshkothadi.autofism3.R
import me.siddheshkothadi.autofism3.ui.Language
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private fun getCurrentLanguageLocale(context: Context): String {
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val currentLanguage = sharedPref.getString(Constants.LANGUAGE_KEY, Language.ENGLISH.locale)
        if (currentLanguage.isNullOrBlank()) {
            return Language.ENGLISH.locale
        }
        return currentLanguage
    }

    fun getDate(timestampString: String): String {
        if (timestampString.isBlank()) return "Loading..."
        val simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        return simpleDateFormat.format(timestampString.toLong())
    }

    fun getTime(timestampString: String): String {
        if (timestampString.isBlank()) return "Loading..."
        val simpleDateFormat = SimpleDateFormat("HH:mm aa", Locale.ENGLISH)
        return simpleDateFormat.format(timestampString.toLong())
    }

    fun getDate(context: Context, timestampString: String): String {
        val currentLanguageLocale = getCurrentLanguageLocale(context)
        if (timestampString.isBlank()) return context.getString(R.string.loading)
        val simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale(currentLanguageLocale))
        return simpleDateFormat.format(timestampString.toLong())
    }

    fun getDateWithoutYear(context: Context, timestampString: String): String {
        val currentLanguageLocale = getCurrentLanguageLocale(context)
        if (timestampString.isBlank()) return context.getString(R.string.loading)
        val simpleDateFormat = SimpleDateFormat("dd MMM", Locale(currentLanguageLocale))
        return simpleDateFormat.format(timestampString.toLong())
    }

    fun getTime(context: Context, timestampString: String): String {
        val currentLanguageLocale = getCurrentLanguageLocale(context)
        if (timestampString.isBlank()) return context.getString(R.string.loading)
        val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale(currentLanguageLocale))
        return simpleDateFormat.format(timestampString.toLong())
    }

    fun getTimeSec(timestamp: Long): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
        return simpleDateFormat.format(timestamp)
    }

    fun getSubmissionTimeStamp(timestamp: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH)
        return simpleDateFormat.format(timestamp)
    }
}