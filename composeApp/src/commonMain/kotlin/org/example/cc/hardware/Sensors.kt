package org.example.cc.hardware

import kotlinx.coroutines.flow.StateFlow

data class ScannedCardResult(val number: String, val expiryDate: String)

interface CardScannerEngine {
    suspend fun scanCard(): ScannedCardResult?
}

data class SensorEvent(val pitch: Float, val roll: Float, val yaw: Float)

interface HardwareSensorEngine {
    val sensorEvents: StateFlow<SensorEvent>
    fun startListening()
    fun stopListening()
}
