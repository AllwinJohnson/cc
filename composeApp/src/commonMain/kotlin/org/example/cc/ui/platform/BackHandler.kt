package org.example.cc.ui.platform

import androidx.compose.runtime.Composable

@Composable
expect fun CCBackHandler(enabled: Boolean = true, onBack: () -> Unit)
