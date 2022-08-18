package me.siddheshkothadi.autofism3

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import me.siddheshkothadi.autofism3.detection.Constants
import me.siddheshkothadi.autofism3.detection.tflite.DetectorFactory
import me.siddheshkothadi.autofism3.detection.tflite.YoloV5Classifier
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    app: FishApplication
) : ViewModel() {
    val detector: YoloV5Classifier? = DetectorFactory.getDetector(app.assets,
        Constants.MODEL_FILE_PATH
    )
}