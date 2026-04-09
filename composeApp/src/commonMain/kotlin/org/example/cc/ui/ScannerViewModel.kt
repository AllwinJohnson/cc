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
    
    private var capturedFront: ScannedCardResult? = null

    fun startScanning() {
        capturedFront = null
        _uiState.value = ScannerUiState.Scanning(ScanSide.FRONT)
    }

    fun onCardDetected(result: ScannedCardResult) {
        val currentState = _uiState.value
        
        if (currentState is ScannerUiState.Scanning) {
            when (currentState.side) {
                ScanSide.FRONT -> {
                    // Front scan must have number and expiry
                    if (result.number.length >= 15 && result.expiryDate.isNotEmpty()) {
                        capturedFront = result
                        _uiState.value = ScannerUiState.Scanning(ScanSide.BACK)
                    }
                }
                ScanSide.BACK -> {
                    // Back scan must have CVV (3-4 digits)
                    if (result.cvv != null && result.cvv.length in 3..4) {
                        val finalResult = capturedFront?.copy(cvv = result.cvv) ?: result
                        _uiState.value = ScannerUiState.Success(finalResult)
                    }
                }
            }
        }
    }


    fun reset() {
        _uiState.value = ScannerUiState.Idle
    }
}
