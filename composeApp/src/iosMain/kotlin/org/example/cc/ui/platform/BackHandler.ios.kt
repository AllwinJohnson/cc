package org.example.cc.ui.platform

import androidx.compose.runtime.Composable

@Composable
actual fun CCBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // No-op for iOS by default as swipe-to-back is handled by navigation
}
