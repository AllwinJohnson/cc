package org.example.cc

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.example.cc.ui.WalletScreen
import org.koin.compose.KoinContext

@Composable
fun App() {
    MaterialTheme {
        KoinContext {
            WalletScreen()
        }
    }
}