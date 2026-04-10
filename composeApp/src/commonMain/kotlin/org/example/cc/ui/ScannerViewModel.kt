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
                    val cleanNumber = result.number.filter { it.isDigit() }
                    // Only require the 15+ digit card number to lock the Front
                    if (cleanNumber.length >= 15) {
                        capturedFront = result
                        _uiState.value = ScannerUiState.Scanning(ScanSide.BACK)
                    }
                }
                ScanSide.BACK -> {
                    val cleanCvv = result.cvv?.filter { it.isDigit() }
                    if (cleanCvv != null && cleanCvv.length in 3..4) {
                        val finalResult = capturedFront?.copy(cvv = cleanCvv) ?: result
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
