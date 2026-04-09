package org.example.cc.hardware

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AndroidCardScannerEngine : CardScannerEngine {
    override suspend fun scanCard(): ScannedCardResult? = null
}

class AndroidHardwareSensorEngine : HardwareSensorEngine {
    private val _tiltData = MutableStateFlow(TiltData(0f, 0f))
    override val tiltData: StateFlow<TiltData> = _tiltData
}
