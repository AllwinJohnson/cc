package org.example.cc.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.example.cc.hardware.ScannedCardResult

enum class ScanSide { FRONT, BACK }

sealed class ScannerUiState {
    object Idle : ScannerUiState()
    data class Scanning(val side: ScanSide = ScanSide.FRONT) : ScannerUiState()
    data class Success(val card: ScannedCardResult) : ScannerUiState()
    data class Error(val message: String) : ScannerUiState()
}

class ScannerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ScannerUiState>(ScannerUiState.Idle)
    val uiState: StateFlow<ScannerUiState> = _uiState

    fun startScanning() {
        _uiState.value = ScannerUiState.Scanning(ScanSide.FRONT)
    }

    fun onCardDetected(result: ScannedCardResult) {
        val currentState = _uiState.value
        if (currentState is ScannerUiState.Scanning && currentState.side == ScanSide.FRONT) {
            // Front scan successful (16 digits should be present)
            _uiState.value = ScannerUiState.Scanning(ScanSide.BACK)
        } else {
            // Back scan completed or second phase done
            _uiState.value = ScannerUiState.Success(result)
        }
    }

    fun reset() {
        _uiState.value = ScannerUiState.Idle
    }
}
