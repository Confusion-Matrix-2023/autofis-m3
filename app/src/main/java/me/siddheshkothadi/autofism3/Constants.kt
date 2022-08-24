package me.siddheshkothadi.autofism3

import me.siddheshkothadi.autofism3.ui.Language

object Constants {
    const val MINIMUM_CONFIDENCE_TF_OD_API = 0.6f
    const val MODEL_FILE_PATH = "yolov5m_Fishnet.tflite"
    const val LANGUAGE_KEY = "language_key"
    const val DEFAULT_LOCALE = "en"
    const val JWT_SECRET = "f00t4f30321@@0439!2#@am"

    val availableLanguages = listOf(
        Language.ENGLISH,
        Language.HINDI,
        Language.Marathi
    )

}