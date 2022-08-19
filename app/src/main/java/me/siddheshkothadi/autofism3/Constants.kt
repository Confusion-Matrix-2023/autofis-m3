package me.siddheshkothadi.autofism3

import me.siddheshkothadi.autofism3.ui.Language

object Constants {
    const val MINIMUM_CONFIDENCE_TF_OD_API = 0.9f
    const val MODEL_FILE_PATH = "yolov5m_On5000.tflite"
    const val LANGUAGE_KEY = "language_key"
    const val DEFAULT_LOCALE = "en"

    val availableLanguages = listOf(
        Language.ENGLISH,
        Language.HINDI,
    )

}