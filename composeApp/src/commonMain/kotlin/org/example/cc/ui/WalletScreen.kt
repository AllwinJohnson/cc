package org.example.cc.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.cc.domain.CardNetwork
import org.example.cc.domain.CardType
import org.example.cc.domain.CreditCard
import org.example.cc.hardware.CameraView
import org.example.cc.hardware.HardwareSensorEngine
import org.example.cc.domain.WalletRepository
import org.koin.compose.koinInject
import kotlin.random.Random
import org.koin.compose.viewmodel.koinViewModel
import kotlin.random.nextInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    repository: WalletRepository = koinInject(),
    sensorEngine: HardwareSensorEngine = koinInject(),
    scannerViewModel: ScannerViewModel = koinViewModel()
) {
    val cards by repository.getAllCards().collectAsState(initial = emptyList())
    val sensorEvent by sensorEngine.sensorEvents.collectAsState()
    val scannerState by scannerViewModel.uiState.collectAsState()
    
    var showScanner by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        sensorEngine.startListening()
        onDispose {
            sensorEngine.stopListening()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val floatingActionButton: @Composable () -> Unit = {
            FloatingActionButton(onClick = {
                showScanner = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Scan Card")
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Holographic Wallet") })
            },
            floatingActionButton = floatingActionButton
        ) { paddingValues ->
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cards) { card ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .holographicTilt(sensorEvent)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Card: ${card.cardNumber}", style = MaterialTheme.typography.titleMedium)
                            Text("Bank: ${card.bankName}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        // Version Footer
        Text(
            text = "v0.3.0-alpha | Phase 3",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )

        // Scanner Bottom Sheet
        if (showScanner) {
            ModalBottomSheet(
                onDismissRequest = { 
                    showScanner = false 
                    scannerViewModel.reset()
                },
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CameraView(
                        onCardScanned = { result ->
                            scannerViewModel.onCardDetected(result)
                            // Auto-insert for now as a demo of detection
                            scope.launch {
                                val dummyCard = CreditCard(
                                    id = Random.nextInt().toString(),
                                    cardNumber = result.number,
                                    cardholderName = "Scanned Card",
                                    expiryDate = result.expiryDate,
                                    cvv = "###",
                                    bankName = "Detected Bank",
                                    network = CardNetwork.VISA,
                                    type = CardType.CREDIT,
                                    accentColorHex = "#222222",
                                    isDetailsOnBack = false
                                )
                                repository.insertCard(dummyCard)
                                showScanner = false
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    ScannerOverlay(
                        sensorEvent = sensorEvent,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
