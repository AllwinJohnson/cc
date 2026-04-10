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
                    
                    // Allow lock if we see 15+ digits OR if we detect a Bank Name (Modern Card)
                    val hasNumbers = cleanNumber.length >= 15
                    val hasBank = !result.bankName.isNullOrEmpty()
                    
                    if (hasNumbers || hasBank) {
                        capturedFront = result
                        _uiState.value = ScannerUiState.Scanning(ScanSide.BACK)
                    }
                }
                ScanSide.BACK -> {
                    val cleanNumber = result.number.filter { it.isDigit() }
                    val cleanCvv = result.cvv?.filter { it.isDigit() }
                    
                    // The back scan MUST have the CVV
                    if (cleanCvv != null && cleanCvv.length in 3..4) {
                        
                        // Merge the data! Take the numbers/expiry from the back if they were missing on the front.
                        val finalResult = capturedFront?.copy(
                            number = if (cleanNumber.length >= 15) cleanNumber else (capturedFront?.number ?: ""),
                            cvv = cleanCvv,
                            expiryDate = if (result.expiryDate.isNotEmpty()) result.expiryDate else (capturedFront?.expiryDate ?: "")
                        ) ?: result
                        
                        _uiState.value = ScannerUiState.Success(finalResult)
                    }
                }
            }
        }
    }

    fun forceFlip() {
        if (_uiState.value is ScannerUiState.Scanning) {
            _uiState.value = ScannerUiState.Scanning(ScanSide.BACK)
        }
    }

    fun resetToIdle() {
        _uiState.value = ScannerUiState.Idle
        capturedFront = null
    }


    fun reset() {
        _uiState.value = ScannerUiState.Idle
    }
}
