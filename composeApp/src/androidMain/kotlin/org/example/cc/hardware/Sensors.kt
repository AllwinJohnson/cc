package org.example.cc.hardware

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent as AndroidSensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.display.DisplayManager
import android.os.Build
import android.view.Display
import android.view.Surface
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AndroidCardScannerEngine : CardScannerEngine {
    override suspend fun scanCard(): ScannedCardResult? = null
}

/**
 * ImageAnalysis.Analyzer that detects card numbers and expiry dates using ML Kit OCR.
 */
@OptIn(ExperimentalGetImage::class)
class CardAnalyzer(
    private val onResult: (ScannedCardResult) -> Unit
) : ImageAnalysis.Analyzer {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    
    // Regex for 16-digit card number (with/without spaces)
    private val cardNumberRegex = Regex("\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}")
    // Regex for Expiry Date (MM/YY)
    private val expiryRegex = Regex("(0[1-9]|1[0-2])\\/([0-9]{2})")

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Extract all text and search for patterns
                    val allText = visionText.text
                    
                    val foundNumber = cardNumberRegex.find(allText)?.value?.replace(Regex("[\\s-]"), "")
                    val foundExpiry = expiryRegex.find(allText)?.value
                    
                    // Simple bank name extraction: take the first line if it's not a number/date
                    val potentialBankName = visionText.textBlocks.firstOrNull()?.text?.split("\n")?.firstOrNull()
                    
                    if (foundNumber != null && isLuhnValid(foundNumber) && foundExpiry != null) {
                        onResult(ScannedCardResult(foundNumber, foundExpiry, potentialBankName))
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}

class AndroidHardwareSensorEngine(private val context: Context) : HardwareSensorEngine, SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    private val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private val _sensorEvents = MutableStateFlow(SensorEvent(0f, 0f, 0f))
    override val sensorEvents: StateFlow<SensorEvent> = _sensorEvents

    private val alpha = 0.15f
    private var currentPitch = 0f
    private var currentRoll = 0f
    private var currentYaw = 0f

    override fun startListening() {
        rotationSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: AndroidSensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            
            val rotation = displayManager.getDisplay(Display.DEFAULT_DISPLAY)?.rotation ?: Surface.ROTATION_0

            var axisX = SensorManager.AXIS_X
            var axisY = SensorManager.AXIS_Y
            when (rotation) {
                Surface.ROTATION_90 -> {
                    axisX = SensorManager.AXIS_Y
                    axisY = SensorManager.AXIS_MINUS_X
                }
                Surface.ROTATION_180 -> {
                    axisX = SensorManager.AXIS_MINUS_X
                    axisY = SensorManager.AXIS_MINUS_Y
                }
                Surface.ROTATION_270 -> {
                    axisX = SensorManager.AXIS_MINUS_Y
                    axisY = SensorManager.AXIS_X
                }
            }

            val remappedMatrix = FloatArray(9)
            SensorManager.remapCoordinateSystem(rotationMatrix, axisX, axisY, remappedMatrix)

            val orientation = FloatArray(3)
            SensorManager.getOrientation(remappedMatrix, orientation)

            val yaw = orientation[0]
            val pitch = orientation[1]
            val roll = orientation[2]

            currentPitch = currentPitch + alpha * (pitch - currentPitch)
            currentRoll = currentRoll + alpha * (roll - currentRoll)
            currentYaw = currentYaw + alpha * (yaw - currentYaw)

            _sensorEvents.value = SensorEvent(
                pitch = currentPitch,
                roll = currentRoll,
                yaw = currentYaw
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
