package org.example.cc.hardware

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class IosCardScannerEngine : CardScannerEngine {
    override suspend fun scanCard(): ScannedCardResult? = null
}

class IosHardwareSensorEngine : HardwareSensorEngine {
    private val _sensorEvents = MutableStateFlow(SensorEvent(0f, 0f, 0f))
    override val sensorEvents: StateFlow<SensorEvent> = _sensorEvents

    override fun startListening() {}
    override fun stopListening() {}
}
