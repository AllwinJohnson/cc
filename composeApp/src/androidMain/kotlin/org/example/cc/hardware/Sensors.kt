package org.example.cc.hardware

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent as AndroidSensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import android.hardware.display.DisplayManager
import android.view.Display
import android.view.Surface

class AndroidCardScannerEngine : CardScannerEngine {
    override suspend fun scanCard(): ScannedCardResult? = null
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
            
            // Safely get display rotation from any context using DisplayManager
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

            // orientation[0] = azimuth (yaw), [1] = pitch, [2] = roll
            val yaw = orientation[0]
            val pitch = orientation[1]
            val roll = orientation[2]

            // Low-pass filter (Alpha smoothing)
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
