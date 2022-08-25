package me.siddheshkothadi.autofism3.ui

enum class Language(val locale: String, val selectText: String, val langName: String) {
    ENGLISH("en", "Choose English", "English"),
    HINDI("hi", "हिंदी चुनें", "हिंदी"),
    MARATHI("mr", "मराठी निवडा", "मराठी"),
    TELUGU("te", "తెలుగు ఎంచుకోండి", "తెలుగు"),
    BENGALI("bn", "বাংলা বেছে নিন", "বাংলা"),
}