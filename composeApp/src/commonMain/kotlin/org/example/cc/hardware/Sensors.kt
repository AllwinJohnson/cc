package org.example.cc.hardware

import kotlinx.coroutines.flow.StateFlow

data class ScannedCardResult(val number: String, val expiryDate: String)

interface CardScannerEngine {
    suspend fun scanCard(): ScannedCardResult?
}

data class TiltData(val roll: Float, val pitch: Float)

interface HardwareSensorEngine {
    val tiltData: StateFlow<TiltData>
}
