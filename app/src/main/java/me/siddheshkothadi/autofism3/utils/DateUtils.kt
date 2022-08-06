package me.siddheshkothadi.autofism3.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
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

    fun getTimeSec(timestamp: Long): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
        return simpleDateFormat.format(timestamp)
    }
}