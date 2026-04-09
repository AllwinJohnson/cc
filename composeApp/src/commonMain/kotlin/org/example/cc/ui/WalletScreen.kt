package org.example.cc.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    var selectedCard by remember { mutableStateOf<CreditCard?>(null) }
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

        AnimatedContent(
            targetState = selectedCard,
            transitionSpec = {
                // Custom 3D-like transition: Slide and Rotate
                (fadeIn() + slideInVertically { it / 2 }) togetherWith
                (fadeOut() + slideOutVertically { -it / 2 })
            }
        ) { targetCard ->
            if (targetCard == null) {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { 
                                Text("WALLET", style = SiltAndStone.Typography().displaySmall) 
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = SiltAndStone.Background
                            )
                        )
                    },
                    bottomBar = {
                        // Silt & Stone "Connected Pill" Bottom Nav
                        Surface(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth()
                                .height(64.dp)
                                .clip(RoundedCornerShape(32.dp)),
                            color = SiltAndStone.PrimaryContainer,
                            tonalElevation = 8.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Scan Button
                                Button(
                                    onClick = { showScanner = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = SiltAndStone.Primary),
                                    shape = RoundedCornerShape(24.dp)
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("NEW CARD", style = SiltAndStone.Typography().labelLarge)
                                }
                            }
                        }
                    },
                    containerColor = SiltAndStone.Background
                ) { paddingValues ->
                    CylinderStack(
                        cards = cards,
                        sensorEvent = sensorEvent,
                        onCardClick = { selectedCard = it },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            } else {
                CardDetailScreen(
                    card = targetCard,
                    sensorEvent = sensorEvent,
                    onBack = { selectedCard = null },
                    onSaveNotes = { newNotes ->
                        repository.updateNotes(targetCard.id, newNotes)
                    }
                )
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
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                dragHandle = null // Editorial Brutalism: No default handles
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SiltAndStone.SurfaceContainer,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CameraView(
                            onCardScanned = { result ->
                                scannerViewModel.onCardDetected(result)
                                
                                val state = scannerViewModel.uiState.value
                                if (state is ScannerUiState.Success) {
                                    scope.launch {
                                        val dummyCard = CreditCard(
                                            id = Random.nextInt().toString(),
                                            cardNumber = result.number,
                                            cardholderName = "Scanned Card",
                                            expiryDate = result.expiryDate,
                                            cvv = "###",
                                            bankName = org.example.cc.domain.BankMatcher.match(result.bankName ?: "Detected Bank"),
                                            network = org.example.cc.domain.CardNetwork.VISA,
                                            type = org.example.cc.domain.CardType.CREDIT,
                                            accentColorHex = "#222222",
                                            isDetailsOnBack = false,
                                            notes = ""
                                        )
                                        repository.insertCard(dummyCard)
                                        showScanner = false
                                        selectedCard = dummyCard
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                        
                        val currentScanSide = when (val state = scannerState) {
                            is ScannerUiState.Scanning -> state.side
                            else -> ScanSide.FRONT
                        }

                        ScannerOverlay(
                            scanSide = currentScanSide,
                            sensorEvent = sensorEvent,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Scan Status Text
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (currentScanSide == ScanSide.FRONT) "SCAN FRONT" else "SCAN BACK",
                                style = SiltAndStone.Typography().headlineMedium,
                                color = SiltAndStone.OnPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
