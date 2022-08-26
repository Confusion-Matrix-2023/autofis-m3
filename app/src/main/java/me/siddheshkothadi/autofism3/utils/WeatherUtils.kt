package me.siddheshkothadi.autofism3.utils

fun String.toCelsius(): String {
    return (toFloat() - 273).toString()
}