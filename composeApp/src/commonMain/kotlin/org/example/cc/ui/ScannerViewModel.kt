package org.example.cc.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.example.cc.hardware.ScannedCardResult

sealed class ScannerUiState {
    object Idle : ScannerUiState()
    object Scanning : ScannerUiState()
    data class Success(val card: ScannedCardResult) : ScannerUiState()
    data class Error(val message: String) : ScannerUiState()
}

class ScannerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ScannerUiState>(ScannerUiState.Idle)
    val uiState: StateFlow<ScannerUiState> = _uiState

    fun startScanning() {
        _uiState.value = ScannerUiState.Scanning
    }

    fun onCardDetected(result: ScannedCardResult) {
        _uiState.value = ScannerUiState.Success(result)
    }

    fun reset() {
        _uiState.value = ScannerUiState.Idle
    }
}
