package org.example.cc.hardware

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.example.cc.ui.ScanSide

/**
 * Platform-agnostic interface for the camera viewfinder.
 */
@Composable
expect fun CameraView(
    onCardScanned: (ScannedCardResult) -> Unit,
    scanSide: ScanSide,
    modifier: Modifier
)

/**
 * Validates a card number using the Luhn Algorithm.
 */
fun isLuhnValid(number: String): Boolean {
    var sum = 0
    var alternate = false
    for (i in number.length - 1 downTo 0) {
        var n = number[i].digitToInt()
        if (alternate) {
            n *= 2
            if (n > 9) n -= 9
        }
        sum += n
        alternate = !alternate
    }
    return (sum % 10 == 0)
}
