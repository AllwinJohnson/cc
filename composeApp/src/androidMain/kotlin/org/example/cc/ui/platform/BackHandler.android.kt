package org.example.cc.ui.platform

import androidx.compose.runtime.Composable

@Composable
actual fun CCBackHandler(enabled: Boolean, onBack: () -> Unit) {
    androidx.activity.compose.BackHandler(enabled, onBack)
}
